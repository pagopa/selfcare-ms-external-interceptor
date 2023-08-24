package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FDApiConnectorImpl implements FDApiConnector {
    private final FDRestClient restClient;

    public FDApiConnectorImpl(FDRestClient restClient){
        this.restClient = restClient;
    }
    @Override
    public String getFdToken() {
        log.trace("getFDToken start");
        String fdToken = restClient.getFDToken();
        log.debug("getFDToken result = {}", fdToken);
        log.trace("getFDToken end");
        return fdToken;
    }
}
