package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationType;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SendFdNotification extends KafkaSend {
    private final Optional<Map<String, String>> producerAllowedTopics;

    public SendFdNotification(@Value("#{${external-interceptor.producer-topics}}") Map<String, String> producerAllowedTopics,  @Autowired
    @Qualifier("fdProducer")KafkaTemplate<String, String> kafkaTemplate) {
        super(kafkaTemplate);
        this.producerAllowedTopics = Optional.ofNullable(producerAllowedTopics);
    }


    @Override
    public void sendInstitutionNotification(Notification notification) throws JsonProcessingException {
        if (validateProductTopic(notification.getProduct())) {
            NotificationToSend notificationToSend = notificationMapper.createInstitutionNotification(notification);
            notificationToSend.setType(NotificationType.ADD_INSTITUTE);
            String institutionNotification = mapper.writeValueAsString(notificationToSend);
            String topic = producerAllowedTopics.get().get(notification.getProduct());
            String logSuccess = String.format("sent notification for token : %s, to FD", notification.getOnboardingTokenId());
            String logFailure = String.format("error during notification sending for token %s: {}, on FD ", notification.getOnboardingTokenId());
            sendNotification(institutionNotification, topic, logSuccess, logFailure);
        }
    }

    @Override
    public void sendUserNotification(UserNotification userNotification) throws JsonProcessingException {
        if (validateProductTopic(userNotification.getProductId())) {
            NotificationToSend notificationToSend = notificationMapper.createUserNotification(userNotification);
            notificationToSend.setType(NotificationType.getNotificationTypeFromRelationshipState(userNotification.getUser().getRelationshipStatus()));
            String userNotificationToSend = mapper.writeValueAsString(notificationToSend);
            String topic = producerAllowedTopics.get().get(userNotification.getProductId());
            String logSuccess = String.format("sent notification for user : %s", userNotification.getUser().getUserId());
            String logFailure = String.format("error during notification sending for user %s: {} ", userNotification.getUser().getUserId());
            sendNotification(userNotificationToSend, topic, logSuccess, logFailure);
        }
    }

    private boolean validateProductTopic(String productId){
        return producerAllowedTopics.isPresent() && producerAllowedTopics.get().containsKey(productId);
    }
}
