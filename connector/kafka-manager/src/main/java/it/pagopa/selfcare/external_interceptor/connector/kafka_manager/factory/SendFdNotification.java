package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationType;
import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.QueueEvent;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserProductDetails;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserToSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SendFdNotification extends KafkaSend {
    private final Optional<Map<String, String>> producerAllowedTopics;

    public SendFdNotification(@Value("#{${external-interceptor.producer-topics}}") Map<String, String> producerAllowedTopics, @Autowired
    @Qualifier("fdProducer") KafkaTemplate<String, String> kafkaTemplate,
                              NotificationMapper notificationMapper,
                              ObjectMapper mapper, ExternalApiConnector externalApiConnector) {
        super(kafkaTemplate, notificationMapper, mapper, null, externalApiConnector);
        this.producerAllowedTopics = Optional.ofNullable(producerAllowedTopics);
    }


    @Override
    public void sendInstitutionNotification(Notification notification, Acknowledgment acknowledgment) throws JsonProcessingException {
        log.trace("sendInstitutionNotification start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "send institution notification = {}", notification);
        if (validateProductTopic(notification.getProduct())) {
            NotificationToSend notificationToSend = notificationMapper.createInstitutionNotification(notification);
            notificationToSend.setType(NotificationType.ADD_INSTITUTE);
            String institutionNotification = mapper.writeValueAsString(notificationToSend);
            String topic = producerAllowedTopics.get().get(notification.getProduct());
            String logSuccess = String.format("sent notification for token : %s, to FD", notification.getOnboardingTokenId());
            String logFailure = String.format("error during notification sending for token: %s, on FD ", notification.getOnboardingTokenId());
            sendNotification(institutionNotification, topic, logSuccess, logFailure, Optional.of(acknowledgment));
        }
        log.trace("sendInstitutionNotification end");
    }

    @Override
    public void sendUserNotification(UserNotification userNotification, Acknowledgment acknowledgment) throws JsonProcessingException {
        log.trace("sendUserNotification start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "send user notification = {}", userNotification);
        if (validateProductTopic(userNotification.getProductId())) {
            NotificationToSend notificationToSend = notificationMapper.createUserNotification(userNotification);
            if (userNotification.getEventType().equals(QueueEvent.UPDATE) && userNotification.getUser().getRelationshipStatus() == null) {
                UserProductDetails userProduct = externalApiConnector.getUserOnboardedProductDetails(userNotification.getUser().getUserId(), userNotification.getInstitutionId(), userNotification.getProductId());
                if(userProduct.getOnboardedProductDetails() != null) {
                    sendUpdateUserEvents(notificationToSend, userProduct);
                    acknowledgment.acknowledge();
                }
            } else {
                notificationToSend.setType(NotificationType.getNotificationTypeFromRelationshipState(userNotification.getUser().getRelationshipStatus()));
                String userNotificationToSend = mapper.writeValueAsString(notificationToSend);
                String topic = producerAllowedTopics.get().get(userNotification.getProductId());
                String logSuccess = String.format("sent notification for user : %s, to FD", userNotification.getUser().getUserId());
                String logFailure = String.format("error during notification sending for user %s: {}, on FD ", userNotification.getUser().getUserId());
                sendNotification(userNotificationToSend, topic, logSuccess, logFailure, Optional.of(acknowledgment));
            }
        }
        log.trace("sendUserNotification end");

    }

    private void sendUpdateUserEvents(NotificationToSend notification, UserProductDetails userProductDetails) {
        log.trace("sendUpdateUserEvents start");
        List<NotificationType> eventTypes = List.of(NotificationType.DELETE_USER, NotificationType.ACTIVE_USER);
        UserToSend userToSend = new UserToSend();
        userToSend.setUserId(userProductDetails.getId());
        userToSend.setRole(userProductDetails.getOnboardedProductDetails().getRole());
        userToSend.setRoles(userProductDetails.getOnboardedProductDetails().getRoles());
        notification.setCreatedAt(userProductDetails.getOnboardedProductDetails().getCreatedAt());
        eventTypes.forEach(type -> {
            notification.setType(type);
            notification.setUser(userToSend);
            String userNotificationToSend = null;
            try {
                userNotificationToSend = mapper.writeValueAsString(notification);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String topic = producerAllowedTopics.get().get(notification.getProduct());
            String logSuccess = String.format("sent notification for user : %s, to FD", notification.getUser().getUserId());
            String logFailure = String.format("error during notification sending for user %s: {}, on FD ", notification.getUser().getUserId());
            sendNotification(userNotificationToSend, topic, logSuccess, logFailure, Optional.empty());
        });
        log.trace("sendUpdateUserEvents End");
    }

    private boolean validateProductTopic(String productId) {
        return producerAllowedTopics.isPresent() && producerAllowedTopics.get().containsKey(productId);
    }
}
