package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.auth.FDToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    FDToken toFDToken(OauthToken token);
}
