package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthorizationHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.external-api.subscriptionKey}")
    private String externalApiSubscriptionKey;
    private static final String PLUS_RAW = "+";
    private static final String PLUS_ENCODED = "%2B";
    @Override
    public void apply(RequestTemplate template) {
        template.removeHeader(HttpHeaders.AUTHORIZATION)
                .header("x-selfcare-uid", "external-interceptor")
                .header("Ocp-Apim-Subscription-Key", externalApiSubscriptionKey);
        final Map<String, Collection<String>> queriesPlusEncoded = new HashMap<>();
        template.queries().forEach((key, value) -> queriesPlusEncoded.put(key, value.stream()
                .map(paramValue -> paramValue.replace(PLUS_RAW, PLUS_ENCODED))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)));
        template.queries(null);
        template.queries(queriesPlusEncoded);
    }
}
