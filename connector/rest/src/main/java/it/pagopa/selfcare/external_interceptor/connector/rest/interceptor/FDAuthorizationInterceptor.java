package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

public class FDAuthorizationInterceptor implements RequestInterceptor {
    private final FDRestClient restClient;
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    public FDAuthorizationInterceptor(FDRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void apply(RequestTemplate template) {
        EncodedParamForm form = new EncodedParamForm(grantType, clientId, clientSecret);
        String jwt = restClient.getFDToken(form).getAccessToken();
        template.removeHeader(HttpHeaders.AUTHORIZATION)
                .removeHeader("Ocp-Apim-Subscription-Key")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt));
    }
}
