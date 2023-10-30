package it.pagopa.selfcare.external_interceptor.connector.model.user;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.Data;

import java.util.List;

@Data
public class UserToSend {
    private String userId;
    private PartyRole role;
    private List<String> roles;
}
