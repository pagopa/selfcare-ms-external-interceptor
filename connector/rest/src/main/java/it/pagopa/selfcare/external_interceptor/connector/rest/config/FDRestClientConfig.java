package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDTokenRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.interceptor.FDAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FDRestClientConfig {
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    @Bean
    public FDAuthorizationInterceptor fdAuthorizationInterceptor(FDTokenRestClient tokenRestClient){
        return new FDAuthorizationInterceptor(tokenRestClient, grantType, clientId, clientSecret);
    }
}
