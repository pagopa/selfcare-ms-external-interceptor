package it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core;

import lombok.Data;

import java.util.List;

@Data
public class TokensResponse {
    List<TokenResponse> items;
}
