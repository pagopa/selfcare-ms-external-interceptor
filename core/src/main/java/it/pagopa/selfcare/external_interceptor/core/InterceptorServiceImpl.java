package it.pagopa.selfcare.external_interceptor.core;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class InterceptorServiceImpl implements InterceptorService {
    private final FDApiConnector fdConnector;
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    public InterceptorServiceImpl(FDApiConnector fdConnector,
                                  @Value("${external-interceptor.fd-token.grant-type}") String grantType,
                                  @Value("${external-interceptor.fd-token.client-id}") String clientId,
                                  @Value("${external-interceptor.fd-token.client-secret}")String clientSecret) {
        this.fdConnector = fdConnector;
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public String getFDToken() {
        log.trace("getFDToken start");
        String fdToken = fdConnector.getFdToken(grantType, clientId, clientSecret);
        log.trace("getFDToken end");
        return fdToken;
    }
}
