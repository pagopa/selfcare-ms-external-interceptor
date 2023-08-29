package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FDAuthorizationInterceptor implements RequestInterceptor {

    private final FDApiConnector fdApiConnector;

    public FDAuthorizationInterceptor(FDApiConnector fdApiConnector) {
        this.fdApiConnector = fdApiConnector;
    }

    @Override
    public void apply(RequestTemplate template) {
            template.removeHeader("Ocp-Apim-Subscription-Key")
                    .removeHeader("x-selfcare-uid")
                    .removeHeader(HttpHeaders.AUTHORIZATION)
                    .removeHeader(HttpHeaders.ACCEPT)
                    .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", fdApiConnector.getToken()))
                    .header(HttpHeaders.ACCEPT, "*/*");
    }
}
