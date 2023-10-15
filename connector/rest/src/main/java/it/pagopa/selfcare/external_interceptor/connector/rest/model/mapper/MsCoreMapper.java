package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core.TokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsCoreMapper {

    Token toToken(TokenResponse model);

    Institution toInstitution(InstitutionResponse model);
}
