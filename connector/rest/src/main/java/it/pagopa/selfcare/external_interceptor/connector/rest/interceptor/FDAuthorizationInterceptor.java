package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FDAuthorizationInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            template.removeHeader(HttpHeaders.AUTHORIZATION)
                    .removeHeader("Ocp-Apim-Subscription-Key")
                    .removeHeader("x-selfcare-uid")
                    .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
        }
    }
}
