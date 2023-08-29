package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import feign.RequestInterceptor;
import it.pagopa.selfcare.commons.connector.rest.interceptor.QueryParamsPlusEncoderInterceptor;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@EnableFeignClients(clients = FDRestClient.class)
@PropertySource("classpath:config/prod-fd-rest-client.properties")
public class FDRestClientConfig {
    @Bean
    public RequestInterceptor queryParamsPlusEncoderInterceptor() {
        return new QueryParamsPlusEncoderInterceptor();
    }
}
