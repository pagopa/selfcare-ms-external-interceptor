package it.pagopa.selfcare.external_interceptor.connector.model.auth;

import lombok.Data;

@Data
public class FDToken {
    private String accessToken;
    private String expiresIn;
}
