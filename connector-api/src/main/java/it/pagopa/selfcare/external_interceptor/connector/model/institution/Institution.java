package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.commons.base.utils.InstitutionType;
import lombok.Data;

import java.util.List;

@Data
public class Institution {
    private String id;
    private String externalId;
    private String originId;
    private String description;
    private String digitalAddress;
    private String address;
    private String zipCode;
    private String taxCode;
    private String taxCodeInvoicing;
    private String origin;
    private Billing billing;
    private InstitutionType institutionType;
    private PspData paymentServiceProvider;
    private DpoData dataProtectionOfficer;
    private List<OnboardedProduct> onboarding;
    private List<GeographicTaxonomy> geographicTaxonomies;
    private CompanyInformations companyInformations;
    private String istatCode;
    private String city;
    private String country;
    private String county;
    private String rea;
    private String shareCapital;
    private String businessRegisterPlace;
    private String supportEmail;
    private String supportPhone;
    private String subUnitCode;
    private String subUnitType;
    private String category;
    private String aooParentCode;
    private RootParent rootParent;
    private String parentDescription;
    private PaAttributes paAttributes;
}
