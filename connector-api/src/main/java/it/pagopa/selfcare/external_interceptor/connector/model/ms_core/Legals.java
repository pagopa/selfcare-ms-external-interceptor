package it.pagopa.selfcare.external_interceptor.connector.model.ms_core;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import it.pagopa.selfcare.external_interceptor.connector.model.constant.Env;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Legals {
    private String partyId;
    private String relationshipId;
    private PartyRole role;
    private Env env;
}
