package it.pagopa.selfcare.external_interceptor.core;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;

public interface InterceptorService {
    String getFDToken();
    OrganizationLightBean checkOrganization(String fiscalCode, String vatNumber);
}
