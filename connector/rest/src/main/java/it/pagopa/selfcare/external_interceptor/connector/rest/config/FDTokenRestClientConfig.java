package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.commons.connector.rest.config.RestClientBaseConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDTokenRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(RestClientBaseConfig.class)
@EnableFeignClients(clients = FDTokenRestClient.class)
@PropertySource("classpath:config/token-fd-rest-client.properties")
public class FDTokenRestClientConfig {
}