package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaSendService {
    void sendUserNotification(UserNotification userNotification, Acknowledgment acknowledgment) throws JsonProcessingException;
}
