package it.pagopa.selfcare.external_interceptor.web.interceptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Component
public class ExternalInterceptorLogRequestInterceptor implements HandlerInterceptor {
    private static final Collection<String> URI_PREFIX_WHITELIST = List.of(
            "/swagger",
            "/v3/api-docs"
    );


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controller) {
        boolean skipLog = URI_PREFIX_WHITELIST.stream()
                .anyMatch(request.getRequestURI()::startsWith);
        if (!skipLog) {
            log.info("Requested {} {}, server Id: {}, controller: {}", request.getMethod(), request.getRequestURI(), request.getServerName(), controller.toString());
        }
        return true;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setTimeZone(TimeZone.getDefault());
        return mapper;
    }
}
