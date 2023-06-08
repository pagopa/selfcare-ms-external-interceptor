package it.pagopa.selfcare.external_interceptor.connector.dao.model;

import it.pagopa.selfcare.onboarding.interceptor.model.institution.AutoApprovalOnboardingRequest;
import it.pagopa.selfcare.onboarding.interceptor.model.kafka.InstitutionOnboardedNotification;
import it.pagopa.selfcare.onboarding.interceptor.model.onboarding.PendingOnboardingNotificationOperations;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@FieldNameConstants(onlyExplicitlyIncluded = true)
@Document("pendingOnboardings")
public class PendingOnboardingEntity implements PendingOnboardingNotificationOperations, Persistable {
    public PendingOnboardingEntity(PendingOnboardingNotificationOperations entity) {
        this();
        id = entity.getId();
        createdAt = entity.getCreatedAt();
        modifiedAt = entity.getModifiedAt();
        request = entity.getRequest();
        notification = entity.getNotification();
        onboardingFailure = entity.getOnboardingFailure();
    }

    @Id
    private String id;
    @CreatedDate
    @FieldNameConstants.Include
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    private AutoApprovalOnboardingRequest request;

    private InstitutionOnboardedNotification notification;

    private String onboardingFailure;

    private boolean isNew = true;
}
