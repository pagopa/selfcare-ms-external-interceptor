package it.pagopa.selfcare.external_interceptor.connector.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.*;
import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.QueueEvent;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotify;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserToSend;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Mapper(componentModel = "spring", imports = {UUID.class, RelationshipState.class })
public interface NotificationMapper {

    @Mapping(target = "id",  expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "user", source = "inbound.user", qualifiedByName = "toUserToSend")
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "createdAt", expression = "java(inbound.getCreatedAt().atOffset(java.time.ZoneOffset.UTC))")
    @Mapping(target = "updatedAt", expression = "java(inbound.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC))")
    NotificationToSend createUserNotification(UserNotification inbound);


    @Mapping(target = "id",  expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "institutionId", source = "inbound.internalIstitutionID")
    @Mapping(target = "institution", source = "inbound.institution", qualifiedByName = "toInstitutionToSend")
    @Mapping(target = "institution.fileName", source = "fileName")
    @Mapping(target = "institution.contentType", source = "contentType")
    NotificationToSend createInstitutionNotification(Notification inbound);



    @Named("toUserToSend")
    @Mapping(target = "roles", expression = "java(toUserRole(user.getProductRole()))")
    UserToSend toUserToSend(UserNotify user);


    @Named("toInstitutionToSend")
    InstitutionToSend toInstitutionToSend(Institution institution);



    @Mapping(target = "id", expression = "java(queueEvent == QueueEvent.ADD ? token.getId() : java.util.UUID.randomUUID().toString())")
    @Mapping(target = "state", expression = "java(queueEvent == QueueEvent.ADD ? RelationshipState.ACTIVE.toString() : (token.getStatus() == RelationshipState.DELETED ? \"CLOSED\" : token.getStatus().toString()))")
    @Mapping(target = "updatedAt", expression = "java(queueEvent == QueueEvent.ADD ? (token.getActivatedAt() != null ? token.getActivatedAt() : token.getCreatedAt()) : (token.getUpdatedAt() != null ? token.getUpdatedAt() : token.getCreatedAt()))")
    @Mapping(target = "closedAt", expression = "java(token.getStatus() == RelationshipState.DELETED ? (token.getDeletedAt() != null ? token.getDeletedAt() : token.getUpdatedAt()) : null)")
    @Mapping(target = "internalIstitutionID", source = "institution.id")
    @Mapping(target = "product", source = "token.productId")
    @Mapping(target = "filePath", source = "token.contractSigned")
    @Mapping(target = "onboardingTokenId", source = "token.id")
    @Mapping(target = "createdAt", expression = "java(queueEvent == QueueEvent.ADD ? (token.getActivatedAt() != null ? token.getActivatedAt() : token.getCreatedAt()) : (token.getUpdatedAt() != null ? token.getUpdatedAt() : token.getCreatedAt()))")
    @Mapping(target = "notificationType", source = "queueEvent")
    @Mapping(target = "fileName", expression = "java(token.getContractSigned() == null ? \"\" : java.nio.file.Paths.get(token.getContractSigned()).getFileName().toString())")
    @Mapping(target = "contentType", expression = "java(token.getContentType() == null ? org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE : token.getContentType())")
    @Mapping(target = "institution", source = "institution")
    @Mapping(target = "billing", expression ="java(getBilling(token, institution))")
    Notification toNotificationToSend(Institution institution, Token token, QueueEvent queueEvent);


    @Named("toUserRole")
    default List<String> toUserRole(String userRole){
        List<String> roles = new ArrayList<>();
        roles.add(userRole);
        return roles;
    }

    default Billing mapBilling(String productId, List<OnboardedProduct> onboardingList, Billing institutionBilling) {
        if (productId != null && onboardingList != null) {
            Optional<OnboardedProduct> onboarding = onboardingList.stream()
                    .filter(o -> productId.equalsIgnoreCase(o.getProductId()))
                    .findFirst();
            return onboarding.get().getBilling() != null ? onboarding.get().getBilling() : institutionBilling;
        }
        return institutionBilling;
    }

    @Named("getBilling")
    default Billing getBilling(Token token, Institution institution){
        if (token.getProductId() != null && institution.getOnboarding() != null) {
            OnboardedProduct onboarding = institution.getOnboarding().stream()
                    .filter(o -> token.getProductId().equalsIgnoreCase(o.getProductId()))
                    .findFirst().orElse(new OnboardedProduct());
            return onboarding.getBilling() != null ? onboarding.getBilling() : institution.getBilling() != null? institution.getBilling(): null;
        }
        return null;
    }
}
