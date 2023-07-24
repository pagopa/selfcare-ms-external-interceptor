package it.pagopa.selfcare.external_interceptor.connector.model.user;

import lombok.Data;

@Data
public class UserNotification {
    private String id;
    private String institutionId;
    private String productId;
    private String onboardingTokenId;
    private String createdAt;
    private String updatedAt;
    private UserNotify user;
}
