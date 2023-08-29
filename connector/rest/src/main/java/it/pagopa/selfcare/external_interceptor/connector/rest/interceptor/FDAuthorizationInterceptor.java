package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDTokenRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
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
public class FDAuthorizationInterceptor implements RequestInterceptor {

    private final FDTokenRestClient fdRestClient;
    private final EncodedParamForm paramForm;
    private static final String PLUS_RAW = "+";
    private static final String PLUS_ENCODED = "%2B";

    public FDAuthorizationInterceptor(FDTokenRestClient fdRestClient,
                                      @Value("${external-interceptor.fd-token.grant-type}")
                                      String grantType,
                                      @Value("${external-interceptor.fd-token.client-id}")
                                      String clientId,
                                      @Value("${external-interceptor.fd-token.client-secret}")
                                      String clientSecret) {
        this.fdRestClient = fdRestClient;
        this.paramForm = new EncodedParamForm(grantType, clientId, clientSecret);
    }

    @Override
    public void apply(RequestTemplate template) {
        template
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", fdRestClient.getFDToken(paramForm).getAccessToken()))
                .header(HttpHeaders.ACCEPT, "*/*");
        final Map<String, Collection<String>> queriesPlusEncoded = new HashMap<>();
        template.queries().forEach((key, value) -> queriesPlusEncoded.put(key, value.stream()
                .map(paramValue -> paramValue.replace(PLUS_RAW, PLUS_ENCODED))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)));
        template.queries(null);
        template.queries(queriesPlusEncoded);
    }
}
