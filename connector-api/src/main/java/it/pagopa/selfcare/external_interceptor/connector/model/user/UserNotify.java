package it.pagopa.selfcare.external_interceptor.connector.model.user;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.Data;

@Data
public class UserNotify {
    private String userId;
    private String name;
    private String familyName;
    private String fiscalCode;
    private String email;
    private PartyRole role;
    private String productRole;
    private RelationshipState relationshipStatus;

}
