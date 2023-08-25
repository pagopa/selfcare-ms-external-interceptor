package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.auth.FDToken;
import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;

public interface FDApiConnector {

    FDToken getFdToken();

    OrganizationLightBean checkOrganization(String fiscalCode, String vatNumber);
}
