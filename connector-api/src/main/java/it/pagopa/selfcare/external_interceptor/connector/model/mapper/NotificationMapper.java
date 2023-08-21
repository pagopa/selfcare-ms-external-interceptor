package it.pagopa.selfcare.external_interceptor.connector.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.InstitutionToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotify;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserToSend;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface NotificationMapper {

    @Mapping(target = "user", source = "inbound.user", qualifiedByName = "toUserToSend")
    @Mapping(target = "product", source = "productId")
    NotificationToSend createUserNotification(UserNotification inbound);


    @Mapping(target = "id",  expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "institutionId", source = "inbound.internalIstitutionID")
    @Mapping(target = "institution", source = "inbound.institution", qualifiedByName = "toInstitutionToSend")
    @Mapping(target = "institution.fileName", source = "fileName")
    @Mapping(target = "institution.contentType", source = "contentType")
    NotificationToSend createInstitutionNotification(Notification inbound);


    @Named("toUserToSend")
    @Mapping(target = "surname", source = "user.familyName")
    @Mapping(target = "taxCode", source = "user.fiscalCode")
    @Mapping(target = "roles", expression = "java(toUserRole(user.getProductRole()))")
    UserToSend toUserToSend(UserNotify user);


    @Named("toInstitutionToSend")
    InstitutionToSend toInstitutionToSend(Institution institution);

    @Named("toUserRole")
    default List<String> toUserRole(String userRole){
        List<String> roles = new ArrayList<>();
        roles.add(userRole);
        return roles;
    }

}
