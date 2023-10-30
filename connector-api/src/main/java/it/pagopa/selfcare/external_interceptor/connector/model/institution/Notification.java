package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.QueueEvent;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Notification {
    private String id;
    private String internalIstitutionID;
    private String product;
    private String state;
    private String filePath;
    private String fileName;
    private String contentType;
    private String onboardingTokenId;
    private String pricingPlan;
    private Institution institution;
    private Billing billing;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime closedAt;
    private QueueEvent notificationType;

}
