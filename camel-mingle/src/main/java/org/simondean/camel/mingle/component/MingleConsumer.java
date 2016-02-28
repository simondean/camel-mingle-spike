package org.simondean.camel.mingle.component;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

public class MingleConsumer extends DefaultConsumer {
  private final MingleEndpoint endpoint;
  private boolean running = false;
  private MingleInnerConsumer client;
  private ScheduledExecutorService executor;

  public MingleConsumer(MingleEndpoint endpoint, Processor processor) {
    super(endpoint, processor);
    this.endpoint = endpoint;
  }

  @Override
  public MingleEndpoint getEndpoint() {
    return (MingleEndpoint)super.getEndpoint();
  }

  @Override
  protected void doStart() throws Exception {
    super.doStart();

    if (!running) {
      createExecutor();
      createClient();
      startClient();
    }
  }

  @Override
  protected void doStop() throws Exception {
    if (running) {
      destroyExecutor();
      destroyClient();
      running = false;
    }

    super.doStop();
  }

  private void createClient() {
    client = new MingleInnerConsumer(this);
  }

  private void startClient() {
    executor.execute(client);
  }

  private void destroyClient() throws IOException {
    client.close();
    client = null;
  }

  private void createExecutor() {
    executor = endpoint.getCamelContext().getExecutorServiceManager().newSingleThreadScheduledExecutor(this, "MingleConsumer");
  }

  private void destroyExecutor() {
    endpoint.getCamelContext().getExecutorServiceManager().shutdownNow(executor);
    executor = null;
  }
}
