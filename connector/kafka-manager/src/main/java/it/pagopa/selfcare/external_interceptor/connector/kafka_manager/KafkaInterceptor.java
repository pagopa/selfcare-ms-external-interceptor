package it.pagopa.selfcare.external_interceptor.connector.kafka_manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.api.InternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationType;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class KafkaInterceptor {

    public static final String NOTIFICATION_CONVERSION_EXCEPTION = "Something went wrong while trying to convert the record";

    private final ObjectMapper mapper;

    private final InternalApiConnector internalApiConnector;

    private final Optional<Map<String, String>> producerAllowedTopics;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final NotificationMapper notificationMapper;

    public KafkaInterceptor(ObjectMapper mapper,
                            InternalApiConnector internalApiConnector,
                            @Value("#{${external-interceptor.producer-topics}}") Map<String, String> producerAllowedTopics,
                            KafkaTemplate<String, String> kafkaTemplate, NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
        log.info("Initializing {}", KafkaInterceptor.class.getSimpleName());
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.internalApiConnector = internalApiConnector;
        this.producerAllowedTopics = Optional.ofNullable(producerAllowedTopics);
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.sc-contracts-read-topic}", containerFactory = "kafkaContractsListenerContainerFactory")
    public void interceptInstitution(ConsumerRecord<String, String> inboundRecord) {
        log.trace("KafkaInterceptor intercept start");
        log.debug("KafKaInterceptor incoming message = {}", inboundRecord);
        Notification notification = null;

        try {
            notification = mapper.readValue(inboundRecord.value(), Notification.class);
            if (validateProductTopic(notification.getProduct())) {
                NotificationToSend notificationToSend = notificationMapper.createInstitutionNotification(notification);
                notificationToSend.setType(NotificationType.ADD_INSTITUTE);
                String institutionNotification = mapper.writeValueAsString(notificationToSend);
                String topic = producerAllowedTopics.get().get(notification.getProduct());
                sendInstitutionNotification(institutionNotification, topic, notification.getOnboardingTokenId());
            }
        } catch (JsonProcessingException e) {
            log.warn(NOTIFICATION_CONVERSION_EXCEPTION, e);
        }
        log.trace("KafkaInterceptor intercept end");
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.sc-users-read-topic}", containerFactory = "kafkaUserListenerContainerFactory")
    public void interceptUsers(ConsumerRecord<String, String> inboundRecord){
        log.trace("KafkaInterceptor intercept users start");
        log.debug("KafKaInterceptor incoming user message = {}", inboundRecord);
        UserNotification notification = null;

        try {
            notification = mapper.readValue(inboundRecord.value(), UserNotification.class);
            if (validateProductTopic(notification.getProductId())) {
                NotificationToSend notificationToSend = notificationMapper.createUserNotification(notification);
                notificationToSend.setType(NotificationType.getNotificationTypeFromRelationshipState(notification.getUser().getRelationshipStatus()));
                String userNotificationToSend = mapper.writeValueAsString(notificationToSend);
                String topic = producerAllowedTopics.get().get(notification.getProductId());
                sendUserNotification(userNotificationToSend, topic, notification.getUser().getUserId());
            }
        } catch (JsonProcessingException e) {
            log.warn(NOTIFICATION_CONVERSION_EXCEPTION, e);
        }
        log.trace("KafkaInterceptor intercept end");
    }

    private boolean validateProductTopic(String productId){
        return producerAllowedTopics.isPresent() && producerAllowedTopics.get().containsKey(productId);
    }

    private void sendInstitutionNotification(String message, String topic, String tokenId) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("sent notification for token : {}", tokenId);
            }
            @Override
            public void onFailure(Throwable ex) {
                log.warn("error during notification sending for token {}: {} ", tokenId, ex.getMessage(), ex);
            }
        });
    }
    private void sendUserNotification(String message, String topic, String userId) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("sent notification for user : {}", userId);
            }
            @Override
            public void onFailure(Throwable ex) {
                log.warn("error during notification sending for user {}: {} ", userId, ex.getMessage(), ex);
            }
        });
    }


}
