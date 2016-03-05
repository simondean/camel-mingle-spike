package org.simondean.spikes.mingleelasticsearch;

import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;
import java.util.Optional;

public abstract class BaseAtomProcessor {
  private final Namespace atomNamespace;

  public BaseAtomProcessor(Namespace atomNamespace) {
    this.atomNamespace = atomNamespace;
  }

  protected Namespace getAtomNamespace() {
    return atomNamespace;
  }

  protected List<Element> getLinks(Element feed) {
    return feed.getChildren("link", atomNamespace);
  }

  protected Optional<Element> findLink(List<Element> links, String rel) {
    return links.stream()
      .filter(link -> doesLinkMatch(link, rel, Optional.empty()))
      .findFirst();
  }

  protected Optional<Element> findLink(List<Element> links, String rel, String type) {
    return links.stream()
      .filter(link -> doesLinkMatch(link, rel, Optional.of(type)))
      .findFirst();
  }

  private boolean doesLinkMatch(Element link, String rel, Optional<String> type) {
    return rel.equals(link.getAttributeValue("rel")) &&
      (!type.isPresent() || type.get().equals(link.getAttributeValue("type")));
  }

  protected String getLinkHref(Element previousLink) {
    return previousLink.getAttributeValue("href");
  }
}
