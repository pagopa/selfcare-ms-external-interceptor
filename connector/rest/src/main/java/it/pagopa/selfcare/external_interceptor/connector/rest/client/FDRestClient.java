package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.rest.interceptor.FDAuthorizationInterceptor;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${rest-client.prod-fd.serviceCode}", url = "${rest-client.prod-fd.base-url}", configuration = FDAuthorizationInterceptor.class)
public interface FDRestClient {
    @GetMapping(value = "${rest-client.prod-fd.check-organization.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    OrganizationLightBeanResponse checkOrganization(@RequestParam("codiceFiscale") String fiscalCode,
                                                    @RequestParam("partitaIva") String vatNumber);
}
