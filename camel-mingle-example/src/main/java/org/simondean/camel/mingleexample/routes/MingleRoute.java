package org.simondean.camel.mingleexample.routes;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MingleRoute extends SpringRouteBuilder {
  @Override
  public void configure() throws Exception {
    from("mingle:localhost:443").to("mock:result");
  }
}
