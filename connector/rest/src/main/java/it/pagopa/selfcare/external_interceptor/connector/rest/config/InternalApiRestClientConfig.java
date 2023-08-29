package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import feign.RequestInterceptor;
import it.pagopa.selfcare.commons.connector.rest.interceptor.QueryParamsPlusEncoderInterceptor;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.InternalApiRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableFeignClients(clients = InternalApiRestClient.class)
@PropertySource("classpath:config/internal-api-rest-client.properties")
public class InternalApiRestClientConfig {
    @Bean
    public RequestInterceptor queryParamsPlusEncoderInterceptor() {
        return new QueryParamsPlusEncoderInterceptor();
    }
}
