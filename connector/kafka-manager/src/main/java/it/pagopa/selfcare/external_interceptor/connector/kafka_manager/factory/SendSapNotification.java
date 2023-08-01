package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationType;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("sapNotificator")
public class SendSapNotification extends KafkaSend {

    public SendSapNotification(@Autowired
                               @Qualifier("sapProducer")
                               KafkaTemplate<String, String> kafkaTemplate,
                               NotificationMapper notificationMapper,
                               ObjectMapper mapper) {
        super(kafkaTemplate, notificationMapper, mapper);
    }

    @Override
    public void sendInstitutionNotification(Notification notification) throws JsonProcessingException {
        log.trace("sendInstitutionNotification start");
        log.debug("send institution notification = {}", notification);
        NotificationToSend notificationToSend = notificationMapper.createInstitutionNotification(notification);
        notificationToSend.setType(NotificationType.ADD_INSTITUTE);
        String institutionNotification = mapper.writeValueAsString(notificationToSend);
        String logSuccess = String.format("sent notification for token : %s, to SAP", notification.getOnboardingTokenId());
        String logFailure = String.format("error during notification sending for token %s: {}, on SAP ", notification.getOnboardingTokenId());
        sendNotification(institutionNotification, "Sc-Contracts-Sap", logSuccess, logFailure);
        log.trace("sendInstitutionNotification end");

    }

    @Override
    public void sendUserNotification(UserNotification userNotification) throws JsonProcessingException {

    }

}
