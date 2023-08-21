package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

@Data
public class InstitutionToSend {

    private String address;
    private String description;
    private String digitalAddress;
    private InstitutionType institutionType;
    private String origin;
    private String originId;
    private String taxCode;
    private String zipCode;
    private String fileName;
    private String contentType;
    private PspData paymentServiceProvider;
    private String istatCode;
    private String city;
    private String country;
    private String county;
    private String subUnitCode;
    private String subUnitType;
    private RootParent rootParent;

}
