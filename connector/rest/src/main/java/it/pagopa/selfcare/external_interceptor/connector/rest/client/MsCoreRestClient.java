package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.rest.config.MsCoreRestClientConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core.TokensResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${rest-client.ms-core.serviceCode}", url = "${rest-client.ms-core.base-url}",configuration = MsCoreRestClientConfig.class)
public interface MsCoreRestClient {

    @GetMapping(value = "${rest-client.ms-core.retrieveTokensByProductId.path}")
    TokensResponse retrieveTokensByProduct(@PathVariable(value = "productId")String productId,
                                          @RequestParam(name = "page")Integer page,
                                          @RequestParam(name = "size")Integer size);

    @GetMapping(value = "${rest-client.ms-core.getInstitutionById.path}")
    InstitutionResponse getInstitutionById(@PathVariable(value = "id")String institutionId);
}
