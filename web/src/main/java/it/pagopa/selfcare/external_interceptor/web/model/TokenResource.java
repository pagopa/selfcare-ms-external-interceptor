package it.pagopa.selfcare.external_interceptor.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResource {
    private String jwt;
}
