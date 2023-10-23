package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.external_interceptor.connector.model.user.UserToSend;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class NotificationToSend {

    private String id;
    private String institutionId;
    private String product;
    private OnboardingStatus state;
    private String onboardingTokenId;
    private OffsetDateTime createdAt;
    private OffsetDateTime closedAt;
    private OffsetDateTime updatedAt;
    private NotificationType type;
    private InstitutionToSend institution;
    private String fileName;
    private String contentType;
    private Billing billing;
    private UserToSend user;

}
