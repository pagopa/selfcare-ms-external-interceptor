package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizationHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.external-api.subscriptionKey}")
    private String externalApiSubscriptionKey;

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-selfcare-uid", "external-interceptor");
        template.header("Ocp-Apim-Subscription-Key", externalApiSubscriptionKey);
    }
}
