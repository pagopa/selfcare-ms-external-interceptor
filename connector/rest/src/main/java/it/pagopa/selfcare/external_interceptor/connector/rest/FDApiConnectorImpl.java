package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.auth.OauthToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FDApiConnectorImpl implements FDApiConnector {
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    private final FDRestClient restClient;
    public FDApiConnectorImpl(FDRestClient restClient){
        this.restClient = restClient;
    }
    @Override
    public OauthToken getFdToken() {
        log.trace("getFDToken start");
        EncodedParamForm form = new EncodedParamForm(grantType, clientId, clientSecret);
        OauthToken fdToken = restClient.getFDToken(form);
        log.debug("getFDToken result = {}", fdToken);
        log.trace("getFDToken end");
        return fdToken;
    }
}
