package it.pagopa.selfcare.external_interceptor.connector.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;

public interface KafkaSapSendService<T> {

    void sendInstitutionNotification(Notification notification, T acknowledgment) throws JsonProcessingException;
}
