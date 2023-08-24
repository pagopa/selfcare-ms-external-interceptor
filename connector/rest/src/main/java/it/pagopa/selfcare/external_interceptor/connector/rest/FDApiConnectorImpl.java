package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
public class FDApiConnectorImpl implements FDApiConnector {
    private final FDRestClient restClient;

    public FDApiConnectorImpl(FDRestClient restClient){
        this.restClient = restClient;
    }
    @Override
    public String getFdToken(String grantType, String clientId, String clientSecret) {
        log.trace("getFDToken start");
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type",grantType);
        formData.add("client_secret", clientSecret);
        formData.add("client_id", clientId);
        String fdToken = restClient.getFDToken(formData);
        log.debug("getFDToken result = {}", fdToken);
        log.trace("getFDToken end");
        return fdToken;
    }
}
