package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${rest-client.prod-fd.serviceCode}", url = "${rest-client.prod-fd.base-url}")
public interface FDRestClient {

    @PostMapping(value = "${rest-client.prod-fd.login.path}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String getFDToken(@RequestParam("grant_type") String grantType,
                      @RequestParam("client_secret") String clientSecret,
                      @RequestParam("client_id") String clientId);
}
