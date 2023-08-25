package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FDAuthorizationInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
            template.header("Ocp-Apim-Subscription-Key", (String) null)
                    .header("x-selfcare-uid", (String) null);
    }
}
