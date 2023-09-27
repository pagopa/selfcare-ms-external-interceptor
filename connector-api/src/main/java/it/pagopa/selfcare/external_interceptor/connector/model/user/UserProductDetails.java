package it.pagopa.selfcare.external_interceptor.connector.model.user;

import lombok.Data;

@Data
public class UserProductDetails {
    private String id;
    private String institutionId;
    private OnboardedProduct onboardedProductDetails;
}
