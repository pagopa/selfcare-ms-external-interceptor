package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.Data;

import java.util.List;

@Data
public class User {

    private String id;
    private String name;
    private String surname;
    private String taxCode;
    private PartyRole role;
    private String email;
    private List<String> roles;

}
