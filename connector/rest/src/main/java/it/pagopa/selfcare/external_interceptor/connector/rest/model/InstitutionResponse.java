package it.pagopa.selfcare.external_interceptor.connector.rest.model;

import it.pagopa.selfcare.commons.base.utils.InstitutionType;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.*;
import lombok.Data;

import java.time.OffsetDateTime;
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
    private Billing billing;
    private String origin;
    private InstitutionType institutionType;
    private PspData paymentServiceProvider;
    private DpoData dataProtectionOfficer;
    private List<OnboardedProduct> onboarding;
    private CompanyInformations companyInformations;
    private List<GeographicTaxonomy> geographicTaxonomies;
    private String rea;
    private String shareCapital;
    private String businessRegisterPlace;
    private String supportEmail;
    private String supportPhone;
    private Boolean imported;
    private String subunitCode;
    private String subunitType;
    private String aooParentCode;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String parentDescription;
    private RootParentResponse rootParent;
    private PaAttributes paAttributes;

}
