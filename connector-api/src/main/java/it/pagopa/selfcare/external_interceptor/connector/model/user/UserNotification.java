package it.pagopa.selfcare.external_interceptor.connector.model.user;

import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.QueueEvent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNotification {
    private String id;
    private String institutionId;
    private String productId;
    private String onboardingTokenId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private QueueEvent eventType;
    private UserNotify user;
}
