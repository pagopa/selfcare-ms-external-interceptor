package it.pagopa.selfcare.external_interceptor.connector.kafka_manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.KafkaSendService;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.KafkaSendStrategyFactory;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.SendSapNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaInterceptor {

    public static final String NOTIFICATION_CONVERSION_EXCEPTION = "Something went wrong while trying to convert the record";
    private final ObjectMapper mapper;
    private final KafkaSendStrategyFactory sendStrategyFactory;
    private final SendSapNotification sapSendService;

    public KafkaInterceptor(ObjectMapper mapper, KafkaSendStrategyFactory sendStrategyFactory, @Qualifier("sapNotificator") SendSapNotification sapSendService) {
        this.mapper = mapper;
        this.sendStrategyFactory = sendStrategyFactory;
        this.sapSendService = sapSendService;
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.sc-contracts-read-topic}", containerFactory = "kafkaContractsListenerContainerFactoryGeneral")
    public void interceptInstitutionGeneral(ConsumerRecord<String, String> inboundRecord, Acknowledgment acknowledgment) {
        log.trace("KafkaInterceptor intercept start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "KafKaInterceptor incoming message = {}", inboundRecord);
        try {
            Notification notification = mapper.readValue(inboundRecord.value(), Notification.class);
            KafkaSendService sendService = sendStrategyFactory.create(notification.getProduct());
            if(sendService!= null)
                sendService.sendInstitutionNotification(notification, acknowledgment);
        } catch (JsonProcessingException e) {
            log.warn(NOTIFICATION_CONVERSION_EXCEPTION, e);
        }

        log.trace("KafkaInterceptor intercept end");
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.sc-contracts-read-topic}", containerFactory = "kafkaContractsListenerContainerFactorySap")
    public void interceptInstitutionSap(ConsumerRecord<String, String> inboundRecord, Acknowledgment acknowledgment) {
        log.trace("KafkaInterceptor intercept start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "KafKaInterceptor incoming message = {}", inboundRecord);
        try {
            Notification notification = mapper.readValue(inboundRecord.value(), Notification.class);
            sapSendService.sendInstitutionNotification(notification, acknowledgment);
        } catch (JsonProcessingException e) {
            log.warn(NOTIFICATION_CONVERSION_EXCEPTION, e);
        }
        log.trace("KafkaInterceptor intercept end");
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.sc-users-read-topic}", containerFactory = "kafkaUserListenerContainerFactory")
    public void interceptUsers(ConsumerRecord<String, String> inboundRecord, Acknowledgment acknowledgment) {
        log.trace("KafkaInterceptor intercept users start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "KafKaInterceptor incoming user message = {}", inboundRecord);

        try {
            UserNotification notification = mapper.readValue(inboundRecord.value(), UserNotification.class);
            KafkaSendService sendService = sendStrategyFactory.create(notification.getProductId());
            if (sendService != null)
                sendService.sendUserNotification(notification, acknowledgment);
        } catch (JsonProcessingException e) {
            log.warn(NOTIFICATION_CONVERSION_EXCEPTION, e);
        }
        log.trace("KafkaInterceptor intercept end");
    }
}
