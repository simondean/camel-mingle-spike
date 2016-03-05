package org.simondean.spikes.mingleelasticsearch;

import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AtomEntryProcessor extends BaseAtomProcessor {
  private final Repository repository;
  private static final Pattern cardNumberPattern = Pattern.compile("^.*/([0-9]+)\\.xml$");

  public AtomEntryProcessor(Namespace atomNamespace, Repository repository) {
    super(atomNamespace);
    this.repository = repository;
  }

  public void process(Element entry) {
    Set<String> categories = getCategories(entry);
    System.out.println(categories);

    if (categories.contains("card")) {
      processCardEvent(entry, categories);
    }
  }

  private void processCardEvent(Element entry, Set<String> categories) {
    List<Element> links = getLinks(entry);
    int cardNumber = getCardNumber(links);

    Card card = repository.getCard(cardNumber);
    boolean isNew = (card == null);

    if (isNew) {
      card = new Card(cardNumber);
    }

    if (isNew) {
      repository.insertCard(card);
    } else {
      repository.updateCard(card);
    }
  }

  private int getCardNumber(List<Element> links) {
    String eventSource = getEventSource(links);
    Matcher matcher = cardNumberPattern.matcher(eventSource);
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
