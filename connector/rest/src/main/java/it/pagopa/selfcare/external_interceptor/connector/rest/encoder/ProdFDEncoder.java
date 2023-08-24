package it.pagopa.selfcare.external_interceptor.connector.rest.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.lang.reflect.Type;
import java.util.Map;

public class ProdFDEncoder implements Encoder {
    private final Encoder delegate;
    private final String grantType;
    private final String clientId;
    private final String clientSecret;

    public ProdFDEncoder(String grantType, String clientId, String clientSecret) {
        HttpMessageConverter<?> formHttpMessageConverter = new FormHttpMessageConverter();
        delegate = new SpringEncoder(() -> new HttpMessageConverters(formHttpMessageConverter));
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        Map<String, Object> requestData = Map.of(
                "grant_type", this.grantType,
                "client_id", this.clientId,
                "client_secret", this.clientSecret
        );

        String formData = formDataToString(requestData);
        delegate.encode(formData, String.class, template);
    }

    private String formDataToString(Map<String, Object> formData) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
}
