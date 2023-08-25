package it.pagopa.selfcare.external_interceptor.core;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
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

    @Override
    public OrganizationLightBean checkOrganization(String fiscalCode, String vatNumber) {
        log.trace("checkOrganization start");
        log.debug("checkOrganization fiscalCode = {}, vatNumber = {}", fiscalCode, vatNumber);
        OrganizationLightBean organization = fdConnector.checkOrganization(fiscalCode, vatNumber);
        log.debug("checkOrganization result = {}", organization);
        log.trace("checkOrganization end");
        return organization;
    }
}
