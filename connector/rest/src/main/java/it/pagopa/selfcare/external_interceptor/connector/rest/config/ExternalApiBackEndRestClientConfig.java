package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.external_interceptor.connector.rest.client.ExternalApiBackEndRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableFeignClients(clients = ExternalApiBackEndRestClient.class)
@PropertySource("classpath:config/external-api-rest-client.properties")
public class ExternalApiBackEndRestClientConfig {
}
