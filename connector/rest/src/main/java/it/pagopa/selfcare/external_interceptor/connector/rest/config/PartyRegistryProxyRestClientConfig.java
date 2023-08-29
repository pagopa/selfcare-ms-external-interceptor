package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import feign.RequestInterceptor;
import it.pagopa.selfcare.commons.connector.rest.interceptor.QueryParamsPlusEncoderInterceptor;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.PartyRegistryProxyRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableFeignClients(clients = PartyRegistryProxyRestClient.class)
@PropertySource("classpath:config/registry-proxy-rest-client.properties")
public class PartyRegistryProxyRestClientConfig {
    @Bean
    public RequestInterceptor queryParamsPlusEncoderInterceptorRegistryProxy() {
        return new QueryParamsPlusEncoderInterceptor();
    }
}
