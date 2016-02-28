package org.simondean.camel.mingle.component;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class MingleComponent extends UriEndpointComponent {
  private static final Logger LOG = LoggerFactory.getLogger(MingleComponent.class);

  public MingleComponent() {
    super(MingleEndpoint.class);
  }

  public MingleComponent(CamelContext context) {
    super(context, MingleEndpoint.class);
  }

  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    URI remainingUri = new URI("http://" + remaining);
    String hostname = remainingUri.getHost();
    int port = remainingUri.getPort();

    MingleEndpoint endpoint = new MingleEndpoint(uri, this);
    endpoint.setHostname(hostname);
    endpoint.setPort(port);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Creating MingleComponent with host {}:{}",
        new Object[]{endpoint.getHostname(), endpoint.getPort()});
    }

    return endpoint;
  }
}
