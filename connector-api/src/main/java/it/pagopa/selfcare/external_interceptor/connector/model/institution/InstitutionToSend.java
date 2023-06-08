package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

@Data
public class InstitutionToSend {

    private String address;
    private String description;
    private String digitalAddress;
    private String origin;
    private String originId;
    private String taxCode;
    private String zipCode;

}
