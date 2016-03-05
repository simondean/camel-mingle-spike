package org.simondean.camel.mingleexample.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MingleRouteTest extends CamelTestSupport {

  @Test
  public void shouldSendHelloWorldMessage() throws Exception {
    MockEndpoint mock = getMockEndpoint("mock:result");
    mock.expectedMessageCount(1);
    mock.expectedBodiesReceived("Hello, World!");
    assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
  }

  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        String host = System.getenv("MINGLE_HOST");
        String port = System.getenv("MINGLE_PORT");
        String projects = System.getenv("MINGLE_PROJECTS");
        from("mingle:" + host + ":" + port + "?projects=" + projects)
          .to("mock:result");
      }
    };
  }
}
