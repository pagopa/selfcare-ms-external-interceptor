package it.pagopa.selfcare.external_interceptor.connector.dao.model;

import it.pagopa.selfcare.external_interceptor.connector.model.dao_operations.ExceptionOperations;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("exceptions")
public class ExceptionsEntity implements ExceptionOperations, Persistable {

    public ExceptionsEntity(ExceptionOperations entity){
        this();
        id = entity.getId();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();
        notification = entity.getNotification();
        exceptionDescription = entity.getExceptionDescription();
    }
    @Id
    private String id;

    private String notification;

    private String exception;

    private String exceptionDescription;

    private boolean isNew = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
