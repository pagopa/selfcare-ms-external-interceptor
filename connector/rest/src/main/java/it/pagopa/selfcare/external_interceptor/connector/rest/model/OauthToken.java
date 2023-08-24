package it.pagopa.selfcare.external_interceptor.connector.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthToken {
    private String jwt;
}
