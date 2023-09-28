package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.rest.config.InternalApiRestClientConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(value = "${rest-client.internal-api.serviceCode}",name = "${rest-client.internal-api.serviceCode}", url = "${rest-client.internal-api.base-url}", configuration = InternalApiRestClientConfig.class)
public interface InternalApiRestClient {

    @GetMapping(value = "${rest-client.internal-api.getInstitution.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    InstitutionResponse getInstitutionById(@PathVariable("id") String institutionId);

    @GetMapping(value = "${rest-client.internal-api.getInstitutionProductUsers.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<UserResponse> getInstitutionProductUsers(@PathVariable("institutionId") String institutionId,
                                                  @PathVariable("productId") String productId);

    @GetMapping(value = "${rest-client.internal-api.aoo.getByCode.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    AooResponse getAooById(@PathVariable(value = "aooId") String aooId);

    @GetMapping(value = "${rest-client.internal-api.uo.getByCode.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    UoResponse getUoById(@PathVariable(value = "uoId") String uoId);

    @GetMapping(value = "${rest-client.internal-api.geo-taxonomies.getByCode.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    GeographicTaxonomiesResponse getExtByCode(@PathVariable(value = "geotax_id") String code);

    @GetMapping(value = "${rest-client.internal-api.registry.getInstitutionById.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    ProxyInstitutionResponse getRegistryInstitutionById(@PathVariable("institutionId") String id);
}
