package org.simondean.spikes.mingleelasticsearch;

import com.google.common.collect.Lists;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeedProcessor {
  private final Element feed;
  private final Namespace atomNamespace;

  public FeedProcessor(Element feed) {
    this.feed = feed;
    atomNamespace = feed.getNamespace();
  }

  public Optional<Element> process() {
    getReverseStream(getEntries(feed)).forEach(entry -> {
      processEntry(entry);
    });

    return getPreviousLink(getLinks(feed));
  }


  private Optional<Element> getPreviousLink(List<Element> links) {
    return links.stream()
      .filter(link -> "previous".equals(link.getAttributeValue("rel")))
      .findFirst();
  }

  private List<Element> getLinks(Element feed) {
    return feed.getChildren("link", atomNamespace);
  }

  private void processEntry(Element entry) {
    Set<String> categories = getCategories(entry);
    System.out.println(categories);

    if (categories.contains("card")) {
      processCardEvent(entry, categories);
    }
  }

  private void processCardEvent(Element entry, Set<String> categories) {
    System.out.println(entry.getChild("id", atomNamespace).getValue());
  }

  private Set<String> getCategories(Element entry) {
    return entry.getChildren("category", atomNamespace).stream()
      .<String>map(category -> category.getAttributeValue("term"))
      .collect(Collectors.toSet());
  }

  private Stream<Element> getReverseStream(List<Element> list) {
    return Lists.reverse(list).stream();
  }

  private List<Element> getEntries(Element feed) {
    return feed.getChildren("entry", atomNamespace);
  }
}
