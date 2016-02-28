package org.simondean.camel.mingle.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class MingleEndpoint extends DefaultEndpoint {
  private String hostname;
  private int port;

  public MingleEndpoint() {
  }

  public MingleEndpoint(String endpointUri, MingleComponent component) {
    super(endpointUri, component);
  }

  @Override
  public Producer createProducer() throws Exception {
    throw new UnsupportedOperationException("Producer not supported");
  }

  @Override
  public Consumer createConsumer(Processor processor) throws Exception {
    return new MingleConsumer(this, processor);
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void doStart() throws Exception {
    super.doStart();

    // TODO: Do stuff
  }

  @Override
  protected void doStop() throws Exception {
    // TODO: Do stuff

    super.doStop();
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
