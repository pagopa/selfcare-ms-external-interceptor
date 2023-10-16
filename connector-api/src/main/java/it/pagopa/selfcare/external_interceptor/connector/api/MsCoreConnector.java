package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;

import java.util.List;

public interface MsCoreConnector {
    List<Token> retrieveTokensByProductId(String productId, Integer page, Integer size);
    Institution getInstitutionById(String institutionId);
}
