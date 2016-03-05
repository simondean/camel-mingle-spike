package org.simondean.camel.mingle.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@UriEndpoint(scheme = "mingle", title = "Mingle", syntax = "mingle:host:port", consumerClass = MingleConsumer.class, label = "api")
public class MingleEndpoint extends DefaultEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MingleEndpoint.class);

  @UriParam
  private String scheme;
  @UriParam
  private String host;
  @UriParam
  private int port;
  @UriParam
  private List<String> projects;
  @UriParam
  private MingleStateRepository stateRepository;

  public MingleEndpoint() {
  }

  public MingleEndpoint(String endpointUri, MingleComponent component) {
    super(endpointUri, component);
  }

  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public List<String> getProjects() {
    return projects;
  }

  public void setProjects(List<String> projects) {
    this.projects = Collections.unmodifiableList(projects);
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

    if (stateRepository == null) {
      stateRepository = new MemoryMingleStateRepository();
      LOG.info("Defaulting to MemoryMingleFeedPositionRepository");
    }
  }

  @Override
  protected void doStop() throws Exception {
    // TODO: Do stuff

    super.doStop();
  }

  public MingleStateRepository getStateRepository() {
    return stateRepository;
  }

  public void setStateRepository(MingleStateRepository stateRepository) {
    this.stateRepository = stateRepository;
  }
}
