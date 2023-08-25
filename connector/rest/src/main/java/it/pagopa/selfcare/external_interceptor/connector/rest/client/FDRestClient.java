package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import feign.Headers;
import feign.Param;
import it.pagopa.selfcare.external_interceptor.connector.rest.interceptor.FDAuthorizationInterceptor;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${rest-client.prod-fd.serviceCode}", url = "${rest-client.prod-fd.base-url}", configuration = FDAuthorizationInterceptor.class)
public interface FDRestClient {
    @PostMapping(value = "${rest-client.prod-fd.login.path}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OauthToken getFDToken(@RequestBody EncodedParamForm encodedParamForm);
    @Headers("Authorization: Bearer {token}")
    @GetMapping(value = "${rest-client.prod-fd.check-organization.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    OrganizationLightBeanResponse checkOrganization(@RequestParam("codiceFiscale") String fiscalCode,
                                                    @RequestParam("partitaIva") String vatNumber,
                                                    @Param("token") String token);
}
