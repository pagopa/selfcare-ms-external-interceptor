package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import it.pagopa.selfcare.external_interceptor.connector.rest.config.FDTokenRestClientConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${rest-client.token-fd.serviceCode}", name = "${rest-client.token-fd.serviceCode}", url = "${rest-client.token-fd.base-url}", configuration = FDTokenRestClientConfig.class)
public interface FDTokenRestClient {
    @PostMapping(value = "${rest-client.token-fd.login.path}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OauthToken getFDToken(@RequestBody EncodedParamForm encodedParamForm);

}
