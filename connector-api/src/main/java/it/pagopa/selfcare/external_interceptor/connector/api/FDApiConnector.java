package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;

public interface FDApiConnector {

    OrganizationLightBean checkOrganization(String fiscalCode, String vatNumber);
}
