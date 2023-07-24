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

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface NotificationMapper {

    @Mapping(target = "user", source = "inbound.user", qualifiedByName = "toUserToSend")
    NotificationToSend createUserNotification(UserNotification inbound);


    @Mapping(target = "id",  expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "institutionId", source = "inbound.internalIstitutionID")
    @Mapping(target = "institution", source = "inbound.institution", qualifiedByName = "toInstitutionToSend")
    NotificationToSend createInstitutionNotification(Notification inbound);


    @Named("toUserToSend")
    @Mapping(target = "surname", source = "user.familyName")
    @Mapping(target = "taxCode", source = "user.fiscalCode")
    @Mapping(target = "roles", source = "user.productRole")
    UserToSend toUserToSend(UserNotify user);


    @Named("toInstitutionToSend")
    InstitutionToSend toInstitutionToSend(Institution institution);
}
