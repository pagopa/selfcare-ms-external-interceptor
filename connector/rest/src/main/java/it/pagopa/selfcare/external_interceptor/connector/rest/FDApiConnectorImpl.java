package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.auth.FDToken;
import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.TokenMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FDApiConnectorImpl implements FDApiConnector {
    private final TokenMapper tokenMapper;
    private final FDMapper fdMapper;
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    private final FDRestClient restClient;
    public FDApiConnectorImpl(TokenMapper tokenMapper, FDMapper fdMapper, FDRestClient restClient){
        this.tokenMapper = tokenMapper;
        this.fdMapper = fdMapper;
        this.restClient = restClient;
    }
    @Override
    public FDToken getFdToken() {
        log.trace("getFDToken start");
        EncodedParamForm form = new EncodedParamForm(grantType, clientId, clientSecret);
        OauthToken oauthToken = restClient.getFDToken(form);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(oauthToken.getAccessToken(), null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.debug("getFDToken result = {}", oauthToken);
        log.trace("getFDToken end");
        return tokenMapper.toFDToken(oauthToken);
    }

    @Override
    public OrganizationLightBean checkOrganization(String fiscalCode, String vatNumber) {
        log.trace("checkOrganization start");
        log.debug("checkOrganization fiscalCode = {}, vatNumber = {}", fiscalCode, vatNumber);
        OrganizationLightBeanResponse response = restClient.checkOrganization(fiscalCode, vatNumber);
        OrganizationLightBean organizationLightBean = fdMapper.toOrganizationLightBean(response);
        return organizationLightBean;
    }
}
