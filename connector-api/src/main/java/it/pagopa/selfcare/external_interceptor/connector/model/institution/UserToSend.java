package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

import java.util.List;

@Data
public class UserToSend {
    private String userId;
    private String name;
    private String surname;
    private String taxCode;
    private String email;
    private String role;
    private List<String> roles;
}
