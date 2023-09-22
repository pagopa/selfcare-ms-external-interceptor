package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.external_interceptor.connector.rest.client.PartyRegistryProxyRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@Import(RestClientBaseConfig.class)
@EnableFeignClients(clients = PartyRegistryProxyRestClient.class)
@PropertySource("classpath:config/registry-proxy-rest-client.properties")
public class PartyRegistryProxyRestClientConfig {
}
