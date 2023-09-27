package it.pagopa.selfcare.external_interceptor.connector.rest.model;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.DpoData;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.GeographicTaxonomy;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.PspData;
import lombok.Data;

import java.util.List;

@Data
public class InstitutionResponse {

    private String id;
    private String externalId;
    private String originId;
    private String description;
    private String digitalAddress;
    private String address;
    private String zipCode;
    private String taxCode;
    private String origin;
    private InstitutionType institutionType;
    private PspData paymentServiceProvider;
    private DpoData dataProtectionOfficer;
    private List<GeographicTaxonomy> geographicTaxonomies;
    private String rea;
    private String shareCapital;
    private String businessRegisterPlace;
    private String supportEmail;
    private String supportPhone;
    private Boolean imported;

}
