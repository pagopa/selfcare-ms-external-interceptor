package it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd;

import lombok.Data;

@Data
public class OrganizationLightBeanResponse {
    private boolean alreadyRegistered;
    private OrganizationResponse organization;
}
