package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;

public interface KafkaSendService {

    void sendInstitutionNotification(Notification notification) throws JsonProcessingException;
    void sendUserNotification(UserNotification userNotification) throws JsonProcessingException;

    default void sendNotification(){

    }
}
