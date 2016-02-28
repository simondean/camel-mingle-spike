package org.simondean.camel.mingle.component;

import org.apache.camel.Exchange;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class MingleInnerConsumer implements Runnable {
  private final MingleConsumer consumer;
  private CloseableHttpClient httpClient;

  public MingleInnerConsumer(MingleConsumer consumer) {
    this.consumer = consumer;
  }

  public void run() {
    httpClient = HttpClients.createDefault();
    Exchange exchange = consumer.getEndpoint().createExchange();
    exchange.getIn().setBody("Hello, World!");
    try {
      consumer.getProcessor().process(exchange);
    } catch (Exception e) {
      exchange.setException(e);
    }
  }

  public void close() throws IOException {
    httpClient.close();
  }
}
