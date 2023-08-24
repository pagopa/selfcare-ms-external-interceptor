package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${rest-client.prod-fd.serviceCode}",  url = "${rest-client.prod-fd.base-url}")
public interface FDRestClient {
    @Bean
    default Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
    @PostMapping(value = "${rest-client.prod-fd.login.path}")
    String getFDToken(@RequestParam("grant_type") String grantType,
                      @RequestParam("client_secret") String clientSecret,
                      @RequestParam("client_id") String clientId);
}
