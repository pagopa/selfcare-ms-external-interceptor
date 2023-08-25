package it.pagopa.selfcare.external_interceptor.web.model;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.Organization;
import lombok.Data;

@Data
public class OrganizationLightBeanResource {
    private boolean alreadyRegistered;
    private Organization organization;
}
