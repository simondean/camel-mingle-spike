package org.simondean.camel.mingle.component;

import org.apache.camel.Exchange;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MingleInnerConsumer implements Runnable {
  private static final String utf8Charset = "UTF-8";
  private final MingleConsumer consumer;
  private final MingleEndpoint endpoint;
  private final MingleStateRepository stateRepository;
  private CloseableHttpClient httpClient;

  public MingleInnerConsumer(MingleConsumer consumer) {
    this.consumer = consumer;
    this.endpoint = consumer.getEndpoint();
    this.stateRepository = endpoint.getStateRepository();
  }

  public void run() {
    httpClient = HttpClients.createDefault();

    endpoint.getProjects().forEach(project -> {
      Exchange exchange = endpoint.createExchange();

      try {
        HttpGet httpGet = new HttpGet(generateUrl(project));

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
          HttpEntity entity = response.getEntity();
          String body = EntityUtils.toString(entity, utf8Charset);
        }

        exchange.getIn().setBody("Hello, World!");
        consumer.getProcessor().process(exchange);
      } catch (Exception e) {
        exchange.setException(e);
      }
    });
  }

  private URI generateUrl(String project) throws URISyntaxException {
    URI lastFeedURL = new URI(stateRepository.getLastFeedURL(project));

    if (lastFeedURL == null) {
      lastFeedURL = new URI(new StringBuilder()
        .append("/api/v2/projects/")
        .append(project)
        .append("/feeds/events.xml?page=1")
        .toString());
    }

    return new URIBuilder(lastFeedURL)
      .setScheme(endpoint.getScheme())
      .setHost(endpoint.getHost())
      .setPort(endpoint.getPort())
      .build();
  }

  public void close() throws IOException {
    httpClient.close();
  }
}
