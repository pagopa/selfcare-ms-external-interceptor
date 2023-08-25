package it.pagopa.selfcare.external_interceptor.connector.rest.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EncodedParamForm {
    private String grant_type;
    private String client_id;
    private String client_secret;
}
