package it.pagopa.selfcare.external_interceptor.web.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(WebConfig.class)
public class WebTestConfig {
}
