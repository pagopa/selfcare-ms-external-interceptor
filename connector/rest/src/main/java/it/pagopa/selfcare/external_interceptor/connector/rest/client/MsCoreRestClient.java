package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.core.generated.openapi.v1.api.MsCoreApi;
import it.pagopa.selfcare.external_interceptor.connector.rest.config.MsCoreRestClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${rest-client.ms-core.serviceCode}", url = "${rest-client.ms-core.base-url}",configuration = MsCoreRestClientConfig.class)
public interface MsCoreRestClient  extends MsCoreApi {

//    @GetMapping(value = "${rest-client.ms-core.retrieveTokensByProductId.path}")
//    TokensResponse retrieveTokensByProduct(@PathVariable(value = "productId")String productId,
//                                          @RequestParam(name = "page")Integer page,
//                                          @RequestParam(name = "size")Integer size);
//
//    @GetMapping(value = "${rest-client.ms-core.getInstitutionById.path}")
//    InstitutionResponse getInstitutionById(@PathVariable(value = "id")String institutionId);
}
