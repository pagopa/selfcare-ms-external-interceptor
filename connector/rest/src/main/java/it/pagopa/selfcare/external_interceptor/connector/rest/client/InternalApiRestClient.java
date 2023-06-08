package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "${rest-client.internal-api.serviceCode}", url = "${rest-client.internal-api.base-url}")
public interface InternalApiRestClient {

    @GetMapping(value = "${rest-client.internal-api.getInstitution.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    InstitutionResponse getInstitutionById(@PathVariable("id") String institutionId);

    @GetMapping(value = "${rest-client.internal-api.getInstitutionProductUsers.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<UserResponse> getInstitutionProductUsers(@PathVariable("institutionId") String institutionId,
                                                  @PathVariable("productId") String productId);

}
