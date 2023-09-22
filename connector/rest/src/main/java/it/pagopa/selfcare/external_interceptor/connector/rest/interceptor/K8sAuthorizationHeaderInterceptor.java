package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class K8sAuthorizationHeaderInterceptor implements RequestInterceptor {

    @Value("${authorization.k8s.token}")
    private String k8sAuthorizationToken;
    @Override
    public void apply(RequestTemplate template) {
        template.removeHeader(HttpHeaders.AUTHORIZATION)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", k8sAuthorizationToken));
    }
}
