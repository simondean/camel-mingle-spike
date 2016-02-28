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
        from("mingle:localhost:443")
          .to("mock:result");
      }
    };
  }
}
