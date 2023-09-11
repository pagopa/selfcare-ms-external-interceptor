package it.pagopa.selfcare.external_interceptor.connector.model.user;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.Data;

@Data
public class UserNotify {
    private String userId;
    private PartyRole role;
    private String productRole;
    private RelationshipState relationshipStatus;

}
