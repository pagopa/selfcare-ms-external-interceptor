package it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizationLightBeanResponse {
    private boolean alreadyRegistered;
    private OrganizationResponse organization;
}
