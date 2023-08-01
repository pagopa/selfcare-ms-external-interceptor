package it.pagopa.selfcare.external_interceptor.connector.model.user;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.Data;

import java.util.List;

@Data
public class UserToSend {
    private String userId;
    private String name;
    private String surname;
    private String taxCode;
    private String email;
    private PartyRole role;
    private List<String> roles;
}
