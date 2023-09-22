package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.user.UserProductDetails;

public interface ExternalApiConnector {

    UserProductDetails getUserOnboardedProductDetails(String userId, String institutionId, String productId);
}
