package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardedProduct {
    private String productId;
    private String tokenId;
    private RelationshipState status;
    private String contract;
    private String pricingPlan;
    private Billing billing;
    private String createdAt;
    private String updatedAt;
}
