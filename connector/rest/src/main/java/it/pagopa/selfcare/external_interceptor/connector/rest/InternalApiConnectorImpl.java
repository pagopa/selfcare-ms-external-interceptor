package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.api.InternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.CompanyInformations;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.User;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.InternalApiRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternalApiConnectorImpl implements InternalApiConnector {

    protected static final String EXTERNAL_ID_IS_REQUIRED = "An institution externalId is required";
    protected static final String PRODUCT_ID_IS_REQUIRED = "A productId is required";
    protected static final String INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";
    private final InternalApiRestClient restClient;

    private static final Function<InstitutionResponse, Institution> INSTITUTION_RESPONSE_TO_INSTITUTION = partyInstitutionResponse -> {
        Institution coreInstitution = new Institution();
        coreInstitution.setId(partyInstitutionResponse.getId());
        coreInstitution.setExternalId(partyInstitutionResponse.getExternalId());
        coreInstitution.setOriginId(partyInstitutionResponse.getOriginId());
        coreInstitution.setOrigin(partyInstitutionResponse.getOrigin());
        coreInstitution.setDescription(partyInstitutionResponse.getDescription());
        coreInstitution.setDigitalAddress(partyInstitutionResponse.getDigitalAddress());
        coreInstitution.setAddress(partyInstitutionResponse.getAddress());
        coreInstitution.setZipCode(partyInstitutionResponse.getZipCode());
        coreInstitution.setTaxCode(partyInstitutionResponse.getTaxCode());
        coreInstitution.setOrigin(partyInstitutionResponse.getOrigin());
        coreInstitution.setInstitutionType(partyInstitutionResponse.getInstitutionType());
        coreInstitution.setPaymentServiceProvider(partyInstitutionResponse.getPaymentServiceProvider());
        coreInstitution.setDataProtectionOfficer(partyInstitutionResponse.getDataProtectionOfficer());
        coreInstitution.setGeographicTaxonomies(partyInstitutionResponse.getGeographicTaxonomies());
        CompanyInformations companyInformations = new CompanyInformations();
        companyInformations.setRea(partyInstitutionResponse.getRea());
        companyInformations.setShareCapital(partyInstitutionResponse.getShareCapital());
        companyInformations.setBusinessRegisterPlace(partyInstitutionResponse.getBusinessRegisterPlace());
        coreInstitution.setCompanyInformations(companyInformations);
        return coreInstitution;
    };

    private Function<UserResponse, User> USER_RESPONSE_TO_USER = userResponse -> {
        User user = new User();
        user.setId(userResponse.getId());
        user.setName(userResponse.getName());
        user.setSurname(userResponse.getSurname());
        user.setTaxCode(userResponse.getFiscalCode());
        user.setEmail(userResponse.getEmail());
        user.setRole(userResponse.getRole());
        user.setRoles(userResponse.getRoles());
        return user;
    };

    @Autowired
    public InternalApiConnectorImpl(InternalApiRestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    public Institution getInstitutionById(String institutionId) {
        log.trace("getInstitutionById start");
        log.debug("getInstitutionById institutionId = {}", institutionId);
        Assert.hasText(institutionId, INSTITUTION_ID_IS_REQUIRED);
        InstitutionResponse response = restClient.getInstitutionById(institutionId);
        Institution result = INSTITUTION_RESPONSE_TO_INSTITUTION.apply(response);
        log.debug("getInstitutionById result = {}", result);
        log.trace("getInstitutionById end");
        return result;
    }

    @Override
    public List<User> getInstitutionProductUsers(String institutionId, String productId) {
        log.trace("getInstitutionProductUsers start");
        log.debug("getInstitutionProductUsers institutionId = {}, productId = {}", institutionId, productId);
        Assert.hasText(institutionId, INSTITUTION_ID_IS_REQUIRED);
        Assert.hasText(productId, PRODUCT_ID_IS_REQUIRED);
        List<UserResponse> userResponse = restClient.getInstitutionProductUsers(institutionId, productId);
        List<User> user = userResponse.stream().map(USER_RESPONSE_TO_USER).collect(Collectors.toList());
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getInstitutionProductUsers user = {}", user);
        log.trace("getInstitutionProductUsers end");
        return user;
    }



}
