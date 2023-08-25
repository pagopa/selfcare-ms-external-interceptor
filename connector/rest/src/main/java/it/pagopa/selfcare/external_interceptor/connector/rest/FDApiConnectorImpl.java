package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.auth.FDToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.TokenMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FDApiConnectorImpl implements FDApiConnector {
    private final TokenMapper tokenMapper;
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    private final FDRestClient restClient;
    public FDApiConnectorImpl(TokenMapper tokenMapper, FDRestClient restClient){
        this.tokenMapper = tokenMapper;
        this.restClient = restClient;
    }
    @Override
    public FDToken getFdToken() {
        log.trace("getFDToken start");
        EncodedParamForm form = new EncodedParamForm(grantType, clientId, clientSecret);
        OauthToken oauthToken = restClient.getFDToken(form);
        log.debug("getFDToken result = {}", oauthToken);
        log.trace("getFDToken end");
        return tokenMapper.toFDToken(oauthToken);
    }
}
