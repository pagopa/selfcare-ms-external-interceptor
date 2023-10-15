package it.pagopa.selfcare.external_interceptor.connector.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;

public interface KafkaSapSendService {

    void sendOldEvents(Notification notification) throws JsonProcessingException;
}
