package it.pagopa.selfcare.external_interceptor.web.config;

import it.pagopa.selfcare.commons.web.config.BaseWebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(BaseWebConfig.class)
@PropertySource("classpath:config/web-config.properties")
class WebConfig{

}
