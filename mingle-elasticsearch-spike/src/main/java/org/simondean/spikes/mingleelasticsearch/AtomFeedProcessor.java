package org.simondean.spikes.mingleelasticsearch;

import com.google.common.collect.Lists;
import org.jdom2.Element;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AtomFeedProcessor extends BaseAtomProcessor {
  private final Element feed;
  private final AtomEntryProcessor entryProcessor;

  public AtomFeedProcessor(Element feed, Repository repository) {
    super(feed.getNamespace());
    this.feed = feed;
    entryProcessor = new AtomEntryProcessor(getAtomNamespace(), repository);
  }

  public Optional<URI> process() throws URISyntaxException {
    getReverseStream(getEntries(feed)).forEach(entry -> {
      entryProcessor.process(entry);
    });

    Optional<Element> previousLink = getPreviousLink(getLinks(feed));

    if (previousLink.isPresent()) {
      return Optional.of(new URI(getLinkHref(previousLink.get())));
    }

    return Optional.empty();
  }

  private Optional<Element> getPreviousLink(List<Element> links) {
    return findLink(links, "previous");
  }

  private Stream<Element> getReverseStream(List<Element> list) {
    return Lists.reverse(list).stream();
  }

  private List<Element> getEntries(Element feed) {
    return feed.getChildren("entry", getAtomNamespace());
  }
}
