package org.simondean.spikes.mingleelasticsearch;

import com.google.common.collect.Lists;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Importer {
  private static final String utf8Charset = "UTF-8";

  public void run(String scheme, String host, int port, String username, String password, String projectName) throws Exception {
    HttpHost httpHost = new HttpHost(host, port, scheme);

    CloseableHttpClient httpClient = HttpClients.custom()
      .setDefaultCredentialsProvider(createCredentialsProvider(httpHost, username, password))
      .build();

    URI pageUri = generateFirstPageUri(projectName);

    while (pageUri != null) {
      System.out.println(pageUri);
      Document document = parseXml(getAtomFeedXml(httpClient, httpHost, pageUri));
      Element feed = document.getRootElement();
      Namespace atomNamespace = feed.getNamespace();

      getReverseStream(getEntries(feed, atomNamespace)).forEach(entry -> {
        processEntry(entry, atomNamespace);
      });

      Optional<Element> previousLink = getPreviousLink(getLinks(feed, atomNamespace), atomNamespace);

      if (previousLink.isPresent()) {
        pageUri = new URI(getLinkHref(previousLink));
      } else {
        pageUri = null;
      }
    }
  }

  private String getLinkHref(Optional<Element> previousLink) {
    return previousLink.get().getAttributeValue("href");
  }

  private Document parseXml(String body) throws JDOMException, IOException {
    SAXBuilder jdomBuilder = new SAXBuilder();

    return jdomBuilder.build(new StringReader(body));
  }

  private String getAtomFeedXml(CloseableHttpClient httpClient, HttpHost httpHost, URI uri) throws URISyntaxException, IOException {
    HttpGet httpGet = new HttpGet(uri);
    String body;

    try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
      HttpEntity entity = response.getEntity();
      body = EntityUtils.toString(entity, utf8Charset);
    }
    return body;
  }

  private Optional<Element> getPreviousLink(List<Element> links, Namespace atomNamespace) {
    return links.stream()
      .filter(link -> "previous".equals(link.getAttributeValue("rel")))
      .findFirst();
  }

  private List<Element> getLinks(Element feed, Namespace atomNamespace) {
    return feed.getChildren("link", atomNamespace);
  }

  private void processEntry(Element entry, Namespace atomNamespace) {
    System.out.println(entry.getChild("id", atomNamespace).getValue());
  }

  private Stream<Element> getReverseStream(List<Element> list) {
    return Lists.reverse(list).stream();
  }

  private List<Element> getEntries(Element feed, Namespace atomNamespace) {
    return feed.getChildren("entry", atomNamespace);
  }

  private CredentialsProvider createCredentialsProvider(HttpHost httpHost, String username, String password) {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(httpHost),
      new UsernamePasswordCredentials(username, password));
    return credentialsProvider;
  }

  private URI generateFirstPageUri(String project) throws URISyntaxException {
    return new URI(new StringBuilder()
      .append("/api/v2/projects/")
      .append(project)
      .append("/feeds/events.xml?page=1")
      .toString());
  }
}
