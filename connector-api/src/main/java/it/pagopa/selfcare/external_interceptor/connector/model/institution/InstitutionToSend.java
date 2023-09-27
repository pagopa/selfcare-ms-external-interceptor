package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.commons.base.utils.InstitutionType;
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
    private String fileName;//fixme: needs to be moved the the upper class
    private String contentType;//fixme: ditto
    private PspData paymentServiceProvider;
    private String istatCode;
    private String city;
    private String country;
    private String county;
    private String subUnitCode;
    private String subUnitType;
    private RootParent rootParent;

}
