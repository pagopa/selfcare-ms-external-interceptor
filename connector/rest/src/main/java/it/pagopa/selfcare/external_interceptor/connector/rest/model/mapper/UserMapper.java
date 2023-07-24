package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.user.User;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "taxCode", source = "fiscalCode")
    User toUser(UserResponse response);
}
