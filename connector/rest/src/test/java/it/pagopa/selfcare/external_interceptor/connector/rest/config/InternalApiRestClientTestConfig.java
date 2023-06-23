package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(InternalApiRestClientConfig.class)
public class InternalApiRestClientTestConfig {
}
