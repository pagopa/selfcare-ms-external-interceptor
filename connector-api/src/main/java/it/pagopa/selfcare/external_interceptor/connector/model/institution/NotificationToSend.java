package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

@Data
public class NotificationToSend {

    private String id;
    private String institutionId;
    private String product;
    private OnboardingStatus state;
    private String onboardingTokenId;
    private String createdAt;
    private String closedAt;
    private String updatedAt;
    private NotificationType type;
    private String fileName;
    private String contentType;
    private InstitutionToSend institution;
    private Billing billing;
    private UserToSend user;

}