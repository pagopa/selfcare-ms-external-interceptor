package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import feign.RequestInterceptor;
import it.pagopa.selfcare.commons.connector.rest.interceptor.QueryParamsPlusEncoderInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/authorization-header-interceptor.properties")
public class AuthorizationHeaderInterceptorConfig {
    @Bean
    public RequestInterceptor queryParamsPlusEncoderInterceptor() {
        return new QueryParamsPlusEncoderInterceptor();
    }
}
