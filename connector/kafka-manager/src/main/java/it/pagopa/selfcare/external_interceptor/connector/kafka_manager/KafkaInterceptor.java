package it.pagopa.selfcare.external_interceptor.connector.kafka_manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.KafkaSendService;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.KafkaSendStrategyFactory;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.SendSapNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaInterceptor {

    public static final String NOTIFICATION_CONVERSION_EXCEPTION = "Something went wrong while trying to convert the record";
    private final ObjectMapper mapper;
    private final KafkaSendStrategyFactory sendStrategyFactory;
    private final SendSapNotification sapSendService;

    public KafkaInterceptor(ObjectMapper mapper, KafkaSendStrategyFactory sendStrategyFactory, @Qualifier("sapNotificator")SendSapNotification sapSendService) {
        this.mapper = mapper;
        this.sendStrategyFactory = sendStrategyFactory;
        this.sapSendService = sapSendService;
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.sc-contracts-read-topic}", containerFactory = "kafkaContractsListenerContainerFactory")
    public void interceptInstitution(ConsumerRecord<String, String> inboundRecord) {
        log.trace("KafkaInterceptor intercept start");
        log.debug("KafKaInterceptor incoming message = {}", inboundRecord);
        Notification notification = null;

        try {
            notification = mapper.readValue(inboundRecord.value(), Notification.class);
            KafkaSendService sendService = sendStrategyFactory.create(notification.getProduct());
            if(sendService!= null)
                sendService.sendInstitutionNotification(notification);
            sapSendService.sendInstitutionNotification(notification);
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
            KafkaSendService sendService = sendStrategyFactory.create(notification.getProductId());
            if(sendService!= null)
                sendService.sendUserNotification(notification);
        } catch (JsonProcessingException e) {
            log.warn(NOTIFICATION_CONVERSION_EXCEPTION, e);
        }
        log.trace("KafkaInterceptor intercept end");
    }


}
