package org.simondean.spikes.mingleelasticsearch;

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
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Importer {
  private static final String utf8Charset = "UTF-8";

  public void run(String scheme, String host, int port, String username, String password, String projectName, String elasticsearchClusterName) throws Exception {
    HttpHost httpHost = new HttpHost(host, port, scheme);
    CloseableHttpClient httpClient = createHttpClient(username, password, httpHost);
    Optional<URI> pageUri = Optional.of(generateFirstPageUri(projectName));
    Repository repository = new ElasticsearchRepository(elasticsearchClusterName);
    repository.init();

    while (pageUri.isPresent()) {
      System.out.println(pageUri);
      Document document = parseXml(getAtomFeedXml(httpClient, httpHost, pageUri.get()));
      Element feed = document.getRootElement();
      AtomFeedProcessor feedProcessor = new AtomFeedProcessor(projectName, feed, repository);
      pageUri = feedProcessor.process();
    }
  }

  private CloseableHttpClient createHttpClient(String username, String password, HttpHost httpHost) {
    return HttpClients.custom()
        .setDefaultCredentialsProvider(createCredentialsProvider(httpHost, username, password))
        .build();
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
