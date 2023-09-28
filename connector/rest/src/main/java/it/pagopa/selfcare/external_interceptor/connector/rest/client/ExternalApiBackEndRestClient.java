package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.model.user.UserProductDetails;
import it.pagopa.selfcare.external_interceptor.connector.rest.config.ExternalApiBackEndRestClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@FeignClient(value = "${rest-client.external-api.serviceCode}", name = "${rest-client.external-api.serviceCode}", url = "${rest-client.external-api.base-url}", configuration = ExternalApiBackEndRestClientConfig.class)
public interface ExternalApiBackEndRestClient {

    @GetMapping(value = "${rest-client.external-api.getUserProductInfo.path}", consumes = APPLICATION_JSON_VALUE)
    UserProductDetails getUserProductDetails(@PathVariable("id")String userId,
                                             @RequestParam("institutionId")String institutionId,
                                             @RequestParam("productId")String productId);
}
