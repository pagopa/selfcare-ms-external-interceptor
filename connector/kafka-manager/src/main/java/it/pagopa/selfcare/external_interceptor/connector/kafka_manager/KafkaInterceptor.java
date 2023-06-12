package it.pagopa.selfcare.external_interceptor.connector.kafka_manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.api.InternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class KafkaInterceptor {

    @Autowired
    private ObjectMapper mapper;

    private final InternalApiConnector internalApiConnector;

    private final Optional<Map<String, String>> producerAllowedTopics;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaInterceptor(InternalApiConnector internalApiConnector,
                            @Value("#{${external-interceptor.producer-topics}}")Map<String, String> producerAllowedTopics){
        this.internalApiConnector = internalApiConnector;
        this.producerAllowedTopics = Optional.ofNullable(producerAllowedTopics);
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.read-topic}")
    public void intercept(ConsumerRecord<String, String> inboundRecord) {
        log.trace("KafkaInterceptor intercept start");
        log.debug("KafKaInterceptor incoming message = {}", inboundRecord);
        Notification notification = null;

        try {
            notification = mapper.readValue(inboundRecord.value(), Notification.class);
            if(producerAllowedTopics.isPresent() && producerAllowedTopics.get().containsKey(notification.getProduct())){
                NotificationToSend notificationToSend = createInstitutionNotification(notification);
                notificationToSend.setType(NotificationType.ADD_INSTITUTE);
                String institutionNotification = mapper.writeValueAsString(notificationToSend);
                String topic = producerAllowedTopics.get().get(notification.getProduct());
                sendNotification(institutionNotification, topic, notification.getOnboardingTokenId());
                List<User> institutionProductUsers = internalApiConnector.getInstitutionProductUsers(notification.getInternalIstitutionID(), notification.getProduct());
                Notification finalNotification = notification;
                institutionProductUsers.forEach(user -> {
                    NotificationToSend userNotification = createUserNotification(finalNotification, user);
                    userNotification.setType(NotificationType.ACTIVE_USER);
                    try {
                        String userNotificationMessage = mapper.writeValueAsString(userNotification);
                        sendNotification(userNotificationMessage, topic, finalNotification.getOnboardingTokenId());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            log.warn("Something went wrong with message processing", e);
        }
    }

    private void sendNotification(String message, String topic, String tokenId){
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
    
    private NotificationToSend createInstitutionNotification(Notification inbound){
        NotificationToSend notificationToSend = new NotificationToSend();

        InstitutionToSend institution = new InstitutionToSend();

        notificationToSend.setId(inbound.getId());
        notificationToSend.setInstitutionId(inbound.getInternalIstitutionID());
        notificationToSend.setProduct(inbound.getProduct());
        notificationToSend.setState(OnboardingStatus.valueOf(inbound.getState()));
        notificationToSend.setOnboardingTokenId(inbound.getOnboardingTokenId());
        notificationToSend.setCreatedAt(inbound.getCreatedAt());
        notificationToSend.setUpdatedAt(inbound.getUpdatedAt());
        notificationToSend.setClosedAt(inbound.getClosedAt());
        notificationToSend.setFileName(inbound.getFileName());
        notificationToSend.setContentType(inbound.getContentType());

        institution.setAddress(inbound.getInstitution().getAddress());
        institution.setOrigin(inbound.getInstitution().getOrigin());
        institution.setTaxCode(inbound.getInstitution().getTaxCode());
        institution.setZipCode(inbound.getInstitution().getZipCode());
        institution.setDigitalAddress(inbound.getInstitution().getDigitalAddress());
        institution.setOriginId(inbound.getInstitution().getOriginId());
        notificationToSend.setInstitution(institution);
        notificationToSend.setBilling(inbound.getBilling());
        return notificationToSend;
    }

    private NotificationToSend createUserNotification(Notification inbound, User user){
        NotificationToSend notificationToSend = new NotificationToSend();

        UserToSend userToSend = new UserToSend();
        userToSend.setUserId(user.getId());
        userToSend.setName(user.getName());
        userToSend.setSurname(user.getSurname());
        userToSend.setRole(user.getRole());
        userToSend.setEmail(user.getEmail());
        userToSend.setRoles(user.getRoles());

        notificationToSend.setId(inbound.getId());
        notificationToSend.setInstitutionId(inbound.getInternalIstitutionID());
        notificationToSend.setProduct(inbound.getProduct());
        notificationToSend.setOnboardingTokenId(inbound.getOnboardingTokenId());
        notificationToSend.setCreatedAt(inbound.getCreatedAt());
        notificationToSend.setUpdatedAt(inbound.getUpdatedAt());
        notificationToSend.setUser(userToSend);

        return notificationToSend;
    }

}
