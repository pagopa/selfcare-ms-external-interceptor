package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.external_interceptor.connector.rest.interceptor.AuthorizationHeaderInterceptor;
import org.springframework.context.annotation.Bean;

public class InternalApiRestClientConfig {

    @Bean
    public AuthorizationHeaderInterceptor internalApiRestClientAuthorizationInterceptor(){
        return new AuthorizationHeaderInterceptor();
    }
}
