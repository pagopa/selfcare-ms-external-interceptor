package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@EnableFeignClients(clients = FDRestClient.class)
@PropertySource("classpath:config/prod-fd-rest-client.properties")
public class FDRestClientConfig {
}
