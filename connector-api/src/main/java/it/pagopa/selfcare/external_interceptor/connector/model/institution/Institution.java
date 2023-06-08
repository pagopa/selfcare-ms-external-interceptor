package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

@Data
public class Institution {
    private InstitutionType institutionType;
    private String description;
    private String digitalAddress;
    private String address;
    private String taxCode;
    private String origin;
    private String originId;
}
