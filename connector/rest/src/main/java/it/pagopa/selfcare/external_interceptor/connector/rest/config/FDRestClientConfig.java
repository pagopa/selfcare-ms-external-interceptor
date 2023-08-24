package it.pagopa.selfcare.external_interceptor.connector.rest.config;

import feign.codec.Encoder;
import it.pagopa.selfcare.commons.connector.rest.config.RestClientBaseConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.encoder.ProdFDEncoder;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${external-interceptor.fd-token.grant-type}")
    private String grantType;
    @Value("${external-interceptor.fd-token.client-id}")
    private String clientId;
    @Value("${external-interceptor.fd-token.client-secret}")
    private String clientSecret;
    @Bean
    public Encoder feignEncoder(){
        return new ProdFDEncoder(grantType, clientId, clientSecret);
    }

}
