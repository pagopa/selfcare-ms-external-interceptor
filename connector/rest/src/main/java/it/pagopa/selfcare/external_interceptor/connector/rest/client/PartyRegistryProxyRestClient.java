package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.rest.config.PartyRegistryProxyRestClientConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.AooResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.GeographicTaxonomiesResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ProxyInstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@FeignClient(value = "${rest-client.party-registry-proxy.serviceCode}", name = "${rest-client.party-registry-proxy.serviceCode}", url = "${rest-client.party-registry-proxy.base-url}", configuration = PartyRegistryProxyRestClientConfig.class)
public interface PartyRegistryProxyRestClient {

    @GetMapping(value = "${rest-client.party-registry-proxy.getInstitutionById.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    ProxyInstitutionResponse getInstitutionById(@PathVariable("institutionId") String id);

    @GetMapping(value = "${rest-client.party-registry-proxy.geo-taxonomies.getByCode.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    GeographicTaxonomiesResponse getExtByCode(@PathVariable(value = "geotax_id") String code);

    @GetMapping(value = "${rest-client.party-registry-proxy.aoo.getByCode.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    AooResponse getAooById(@PathVariable(value = "aooId") String aooId);

    @GetMapping(value = "${rest-client.party-registry-proxy.uo.getByCode.path}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    UoResponse getUoById(@PathVariable(value = "uoId") String uoId);
}
