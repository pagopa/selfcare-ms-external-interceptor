package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.external_interceptor.connector.rest.interceptor.K8sAuthorizationHeaderInterceptor;
import org.springframework.context.annotation.Bean;

public class PartyRegistryProxyRestClientConfig {
    @Bean
    public K8sAuthorizationHeaderInterceptor partyRegistryK8sAuthorizationRequestInterceptor(){
        return new K8sAuthorizationHeaderInterceptor();
    }
}
