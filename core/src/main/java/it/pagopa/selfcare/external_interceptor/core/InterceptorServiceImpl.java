package it.pagopa.selfcare.external_interceptor.core;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class InterceptorServiceImpl implements InterceptorService {
    private final FDApiConnector fdConnector;

    public InterceptorServiceImpl(FDApiConnector fdConnector) {
        this.fdConnector = fdConnector;

    }

    @Override
    public String getFDToken() {
        log.trace("getFDToken start");
        String fdToken = fdConnector.getFdToken().getAccessToken();
        log.trace("getFDToken end");
        return fdToken;
    }
}
