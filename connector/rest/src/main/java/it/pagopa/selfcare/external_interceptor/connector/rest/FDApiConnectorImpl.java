package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FDApiConnectorImpl implements FDApiConnector {
    private final FDMapper fdMapper;
    @Getter
    private static String token;
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    private final FDRestClient restClient;
    public FDApiConnectorImpl(FDMapper fdMapper, FDRestClient restClient){
        this.fdMapper = fdMapper;
        this.restClient = restClient;
    }

    @Override
    public OrganizationLightBean checkOrganization(String fiscalCode, String vatNumber) {
        log.trace("checkOrganization start");
        log.debug("checkOrganization fiscalCode = {}, vatNumber = {}", fiscalCode, vatNumber);
        EncodedParamForm form = new EncodedParamForm(grantType, clientId, clientSecret);
        OauthToken oauthToken = restClient.getFDToken(form);
        token = oauthToken.getAccessToken();
        OrganizationLightBeanResponse response = restClient.checkOrganization(fiscalCode, vatNumber);
        OrganizationLightBean organizationLightBean = fdMapper.toOrganizationLightBean(response);
        log.debug("checkOrganization result = {}", organizationLightBean);
        log.trace("checkOrganization end");
        return organizationLightBean;
    }
}
