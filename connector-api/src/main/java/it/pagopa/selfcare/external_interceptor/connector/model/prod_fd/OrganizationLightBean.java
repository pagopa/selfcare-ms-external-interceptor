package it.pagopa.selfcare.external_interceptor.connector.model.prod_fd;

import lombok.Data;

@Data
public class OrganizationLightBean {
    private boolean alreadyRegistered;
    private Organization organization;
}
