package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDTokenRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FDAuthorizationInterceptor implements RequestInterceptor {

    private final FDTokenRestClient fdRestClient;
    private final EncodedParamForm paramForm;

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
        template.removeHeader("Ocp-Apim-Subscription-Key")
                .removeHeader("x-selfcare-uid")
                .removeHeader(HttpHeaders.AUTHORIZATION)
                .removeHeader(HttpHeaders.ACCEPT)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", fdRestClient.getFDToken(paramForm).getAccessToken()))
                .header(HttpHeaders.ACCEPT, "*/*");
    }
}
