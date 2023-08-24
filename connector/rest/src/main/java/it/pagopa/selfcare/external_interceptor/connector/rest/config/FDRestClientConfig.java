package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import it.pagopa.selfcare.commons.connector.rest.config.RestClientBaseConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;


@Configuration
@Import(RestClientBaseConfig.class)
@EnableFeignClients(clients = FDRestClient.class)
@PropertySource("classpath:config/prod-fd-rest-client.properties")
public class FDRestClientConfig {
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
