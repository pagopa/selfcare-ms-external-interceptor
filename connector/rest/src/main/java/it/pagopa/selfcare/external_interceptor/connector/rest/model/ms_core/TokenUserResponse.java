package it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenUserResponse {
    private String userId;
    private PartyRole role;
}
