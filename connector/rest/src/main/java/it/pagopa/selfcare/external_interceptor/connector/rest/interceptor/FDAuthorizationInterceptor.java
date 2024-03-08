package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDTokenRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
public class FDAuthorizationInterceptor implements RequestInterceptor {

    private final FDTokenRestClient fdRestClient;
    private final EncodedParamForm paramForm;

    public FDAuthorizationInterceptor(FDTokenRestClient fdRestClient,
                                      String grantType,
                                      String clientId,
                                      String clientSecret) {
        this.fdRestClient = fdRestClient;
        this.paramForm = new EncodedParamForm(grantType, clientId, clientSecret);
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", fdRestClient.getFDToken(paramForm).getAccessToken()))
                .header(HttpHeaders.ACCEPT, "*/*");
    }
}
