package it.pagopa.selfcare.external_interceptor.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

@Configuration
@ComponentScan(basePackages = {"it.pagopa.selfcare.external_interceptor.web.interceptor"})
class WebConfig implements WebMvcConfigurer {
    private final Collection<HandlerInterceptor> interceptors;


    public WebConfig(Collection<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptors != null) {
            interceptors.forEach(registry::addInterceptor);
        }
    }

}
