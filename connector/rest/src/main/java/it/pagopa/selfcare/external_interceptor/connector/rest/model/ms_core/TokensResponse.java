package it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponse {
    List<TokenResponse> items;
}
