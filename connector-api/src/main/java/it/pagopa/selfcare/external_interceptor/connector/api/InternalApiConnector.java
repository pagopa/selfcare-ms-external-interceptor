package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.onboarding.interceptor.model.institution.AutoApprovalOnboardingRequest;
import it.pagopa.selfcare.onboarding.interceptor.model.institution.Institution;
import it.pagopa.selfcare.onboarding.interceptor.model.institution.User;
import it.pagopa.selfcare.onboarding.interceptor.model.product.Product;

import java.util.List;

public interface InternalApiConnector {

    Institution getInstitutionById(String institutionId);

    List<User> getInstitutionProductUsers(String institutionId, String productId);


}
