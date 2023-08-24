package it.pagopa.selfcare.external_interceptor.connector.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncodedParamForm {
    private String grantType;
    private String clientId;
    private String clientSecret;
}
