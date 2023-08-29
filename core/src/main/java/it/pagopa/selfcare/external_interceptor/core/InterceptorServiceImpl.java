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
    public boolean checkOrganization(String fiscalCode, String vatNumber) {
        log.trace("checkOrganization start");
        log.debug("checkOrganization fiscalCode = {}, vatNumber = {}", fiscalCode, vatNumber);
        boolean alreadyRegistered = fdConnector.checkOrganization(fiscalCode, vatNumber).isAlreadyRegistered();
        log.debug("checkOrganization result = {}", alreadyRegistered);
        log.trace("checkOrganization end");
        return alreadyRegistered;
    }
}
