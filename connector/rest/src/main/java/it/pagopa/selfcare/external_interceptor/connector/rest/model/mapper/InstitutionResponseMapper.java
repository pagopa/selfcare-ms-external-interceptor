package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstitutionResponseMapper {

    @Mapping(target = "companyInformations.rea", source = "rea" )
    @Mapping(target = "companyInformations.shareCapital", source = "shareCapital" )
    @Mapping(target = "companyInformations.businessRegisterPlace", source = "businessRegisterPlace" )
    @Mapping(target = "subUnitType", source = "subunitType")
    @Mapping(target = "subUnitCode", source = "subunitCode")
    Institution toInstitution(InstitutionResponse response);


}
