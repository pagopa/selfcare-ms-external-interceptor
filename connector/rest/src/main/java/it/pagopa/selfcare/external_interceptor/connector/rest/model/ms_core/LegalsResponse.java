package it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import it.pagopa.selfcare.external_interceptor.connector.model.constant.Env;
import lombok.Data;

@Data
public class LegalsResponse {
    private String partyId;
    private String relationshipId;
    private PartyRole role;
    private Env env;
}
