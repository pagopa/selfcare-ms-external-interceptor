package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import it.pagopa.selfcare.external_interceptor.connector.rest.config.FDRestClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Qualifier("prod-fd")
@FeignClient(name = "${rest-client.prod-fd.serviceCode}", configuration = FDRestClientConfig.class, url = "${rest-client.prod-fd.base-url}")
public interface FDRestClient {
    @Bean
    default Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
    @PostMapping(value = "${rest-client.prod-fd.login.path}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getFDToken(@RequestParam("grant_type") String grantType,
                      @RequestParam("client_secret") String clientSecret,
                      @RequestParam("client_id") String clientId);
}
