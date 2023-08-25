package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.external_interceptor.connector.rest.FDApiConnectorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FDAuthorizationInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
            template.removeHeader("Ocp-Apim-Subscription-Key")
                    .removeHeader("x-selfcare-uid")
                    .removeHeader(HttpHeaders.AUTHORIZATION)
                    .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", FDApiConnectorImpl.token));
    }
}
