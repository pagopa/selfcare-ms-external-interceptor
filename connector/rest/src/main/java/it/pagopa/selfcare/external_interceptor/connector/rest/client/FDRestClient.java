package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import feign.codec.Encoder;
import it.pagopa.selfcare.external_interceptor.connector.rest.encoder.ProdFDEncoder;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${rest-client.prod-fd.serviceCode}",  url = "${rest-client.prod-fd.base-url}")
public interface FDRestClient {
    @Bean
    default Encoder feignEncoder(){
        return new ProdFDEncoder();
    }
    @PostMapping(value = "${rest-client.prod-fd.login.path}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getFDToken(@RequestBody EncodedParamForm encodedParamForm);
}
