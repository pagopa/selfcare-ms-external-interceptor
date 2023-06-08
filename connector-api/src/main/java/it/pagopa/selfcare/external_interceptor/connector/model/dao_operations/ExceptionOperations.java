package it.pagopa.selfcare.external_interceptor.connector.model.dao_operations;

import java.time.LocalDateTime;

public interface ExceptionOperations {
    String getId();
    void setId(String id);
    String getException();
    void setException(String exception);
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);
    String getNotification();
    void setNotification(String notification);
    String getExceptionDescription();
    void setExceptionDescription(String exceptionDescription);
}
