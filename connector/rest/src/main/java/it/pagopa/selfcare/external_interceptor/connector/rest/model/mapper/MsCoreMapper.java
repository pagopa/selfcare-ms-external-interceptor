package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MsCoreMapper {

    Token toToken(TokenResponse model);

    @Mapping(target = "subUnitType", source = "model.subunitType")
    @Mapping(target = "subUnitCode", source = "model.subunitCode")
    @Mapping(target = "companyInformations.rea", source = "rea" )
    @Mapping(target = "companyInformations.shareCapital", source = "shareCapital" )
    @Mapping(target = "companyInformations.businessRegisterPlace", source = "businessRegisterPlace" )
    Institution toInstitution(InstitutionResponse model);
}
