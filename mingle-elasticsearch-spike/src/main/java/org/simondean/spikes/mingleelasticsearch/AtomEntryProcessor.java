package org.simondean.spikes.mingleelasticsearch;

import org.jdom2.Element;
import org.jdom2.Namespace;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AtomEntryProcessor extends BaseAtomProcessor {
  private final String projectName;
  private final Repository repository;
  private static final Pattern eventIdPattern = Pattern.compile("/([0-9]+)$");
  private static final Pattern cardNumberPattern = Pattern.compile("/([0-9]+)\\.xml$");
  private static final Namespace mingleNamespace = Namespace.getNamespace("http://www.thoughtworks-studios.com/ns/mingle");

  public AtomEntryProcessor(String projectName, Namespace atomNamespace, Repository repository) {
    super(atomNamespace);
    this.projectName = projectName;
    this.repository = repository;
  }

  public void process(Element entry) throws IOException {
    Instant updatedInstant = DateTimeFormatter.ISO_INSTANT.parse(entry.getChildText("updated", getAtomNamespace()), Instant::from);
    Set<String> categories = getCategories(entry);
    System.out.println(categories);

    if (categories.contains("card")) {
      processCardEvent(entry, updatedInstant, categories);
    }
  }

  private void processCardEvent(Element entry, Instant updatedInstant, Set<String> categories) throws IOException {
    List<Element> links = getLinks(entry);
    int cardNumber = getCardNumber(links);

    Card card = repository.getCard(projectName, cardNumber);

    if (card == null) {
      card = new Card();
      card.setProjectName(projectName);
      card.setNumber(cardNumber);
    }

    card.setTimestamp(updatedInstant);

    Element changesParent = entry.getChild("content", getAtomNamespace()).getChild("changes", mingleNamespace);
    List<Element> changes = changesParent.getChildren("change", mingleNamespace);

    Optional<Element> nameChange = findChangeForChangeType(changes, "name-change");

    if (nameChange.isPresent()) {
      card.setName(nameChange.get().getChildText("new_value", mingleNamespace));
    }

    List<Element> propertyChanges = findChangesForChangeType(changes, "property-change");

    for (Element propertyChange : propertyChanges) {
      Element propertyDefinition = propertyChange.getChild("property_definition", mingleNamespace);
      String propertyName = propertyDefinition.getChildText("name", mingleNamespace);
      // TODO: Handle data types rather than treating everything as a string
      String propertyValue = propertyChange.getChildText("new_value", mingleNamespace);

//      CardProperty cardProperty = card.getProperties().get(propertyName);
//
//      if (cardProperty == null) {
//        cardProperty = new CardProperty();
//        cardProperty.setName(propertyName);
//        card.getProperties().put(propertyName, cardProperty);
//      }
//
//      // TODO: Handle data types rather than treating everything as a string
//      cardProperty.setValue(propertyValue);

      card.getProperties().put(propertyName, propertyValue);
    }

    int eventId = getEventId(entry.getChildText("id", getAtomNamespace()));
    repository.upsertCard(card);
    repository.insertCardEvent(eventId, card);
  }

  private Optional<Element> findChangeForChangeType(List<Element> changes, String type) {
    return changes.stream()
      .filter(change -> doesChangeMatch(change, type))
      .findFirst();
  }

  private List<Element> findChangesForChangeType(List<Element> changes, String type) {
    return changes.stream()
      .filter(change -> doesChangeMatch(change, type))
      .collect(Collectors.toList());
  }

  private boolean doesChangeMatch(Element change, String type) {
    return type.equals(change.getAttributeValue("type"));
  }

  private int getEventId(String id) {
    Matcher matcher = eventIdPattern.matcher(id);
    matcher.find();
    return Integer.valueOf(matcher.group(1));
  }

  private int getCardNumber(List<Element> links) {
    String eventSource = getEventSource(links);
    Matcher matcher = cardNumberPattern.matcher(eventSource);
    matcher.find();
    return Integer.valueOf(matcher.group(1));
  }

  private String getEventSource(List<Element> links) {
    Optional<Element> link = findLink(links, "http://www.thoughtworks-studios.com/ns/mingle#event-source", "application/vnd.mingle+xml");
    return getLinkHref(link.get());
  }

  private Set<String> getCategories(Element entry) {
    return entry.getChildren("category", getAtomNamespace()).stream()
      .<String>map(category -> category.getAttributeValue("term"))
      .collect(Collectors.toSet());
  }
}
