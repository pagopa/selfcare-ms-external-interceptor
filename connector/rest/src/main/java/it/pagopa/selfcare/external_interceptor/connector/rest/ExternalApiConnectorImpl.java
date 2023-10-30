package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserProductDetails;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.ExternalApiBackEndRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExternalApiConnectorImpl implements ExternalApiConnector {
    private final ExternalApiBackEndRestClient restClient;

    public ExternalApiConnectorImpl(ExternalApiBackEndRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public UserProductDetails getUserOnboardedProductDetails(String userId, String institutionId, String productId) {
        log.trace("getUserOnboardedProductDetails start");
        log.debug("getUserOnboardedProductDetails userId = {}, institutionId = {}, productId = {}", userId, institutionId, productId);
        UserProductDetails userProductDetails = restClient.getUserProductDetails(userId, institutionId, productId);
        log.debug("getUserOnboardedProductDetails result = {}", userProductDetails);
        log.trace("getUserOnboardedProductDetails end");
        return userProductDetails;
    }
}
