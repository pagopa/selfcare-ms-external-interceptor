package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

//@Service
@Slf4j
public class RegistryProxyAuthorizationHeaderInterceptor implements RequestInterceptor {

    @Value("${authorization.k8s.token}")
    private String k8sAuthorizationToken;
    @Override
    public void apply(RequestTemplate template) {
        template.header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", k8sAuthorizationToken));
    }
}
