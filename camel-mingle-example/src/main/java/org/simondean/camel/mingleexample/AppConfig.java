package org.simondean.camel.mingleexample;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.apache.camel.spring.javaconfig.CamelConfiguration;

@Configuration
@ComponentScan("org.simondean.camel.mingleexample.routes")
public class AppConfig extends CamelConfiguration {
}
