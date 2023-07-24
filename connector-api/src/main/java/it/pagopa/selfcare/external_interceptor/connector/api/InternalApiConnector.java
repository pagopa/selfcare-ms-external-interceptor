package it.pagopa.selfcare.external_interceptor.connector.api;


import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.user.User;

import java.util.List;

public interface InternalApiConnector {

    Institution getInstitutionById(String institutionId);

    List<User> getInstitutionProductUsers(String institutionId, String productId);


}
