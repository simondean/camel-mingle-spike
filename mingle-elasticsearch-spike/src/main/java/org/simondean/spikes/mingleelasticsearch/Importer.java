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
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

public class Importer {
  private static final String utf8Charset = "UTF-8";

  public void run(String scheme, String host, int port, String username, String password, String projectName) throws Exception {
    HttpHost httpHost = new HttpHost(host, port, scheme);

    CloseableHttpClient httpClient = HttpClients.custom()
      .setDefaultCredentialsProvider(createCredentialsProvider(httpHost, username, password))
      .build();

    HttpGet httpGet = new HttpGet(generateUrl(projectName));

    try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
      HttpEntity entity = response.getEntity();
      String body = EntityUtils.toString(entity, utf8Charset);

      SAXBuilder jdomBuilder = new SAXBuilder();

      Document document = jdomBuilder.build(new StringReader(body));
      Element feed = document.getRootElement();
      Namespace atomNamespace = feed.getNamespace();

      feed.getChildren("entry", atomNamespace).stream().forEach(entry -> {
        System.out.println(entry.getChild("id", atomNamespace).getValue());
      });

      //System.out.println(body);
    }
  }

  private CredentialsProvider createCredentialsProvider(HttpHost httpHost, String username, String password) {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(httpHost),
      new UsernamePasswordCredentials(username, password));
    return credentialsProvider;
  }

  private URI generateUrl(String project) throws URISyntaxException {
    return new URI(new StringBuilder()
      .append("/api/v2/projects/")
      .append(project)
      .append("/feeds/events.xml?page=1")
      .toString());
  }
}
