package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.api.RegistryProxyConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;

@Slf4j
@Scope("prototype")
@Service
public abstract class KafkaSend implements KafkaSendService {
    KafkaTemplate<String, String> kafkaTemplate;
    final NotificationMapper notificationMapper;
    final ObjectMapper mapper;
    final RegistryProxyConnector registryProxyConnector;
    final ExternalApiConnector externalApiConnector;

    KafkaSend(KafkaTemplate<String, String> kafkaTemplate, NotificationMapper notificationMapper, ObjectMapper mapper, RegistryProxyConnector registryProxyConnector, ExternalApiConnector externalApiConnector){
        this.kafkaTemplate = kafkaTemplate;
        this.notificationMapper = notificationMapper;
        this.mapper = mapper;
        this.registryProxyConnector = registryProxyConnector;
        this.externalApiConnector = externalApiConnector;
    }

    void sendNotification(String message, String topic, String successLog, String logFailure, Optional<Acknowledgment> acknowledgment) {
        log.trace("sendNotification start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "send notification message = {}, to topic: {}", message, topic);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info(successLog);
                acknowledgment.get().acknowledge();
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn(logFailure, ex.getMessage(), ex);
                acknowledgment.get().nack(60000);
            }
        });
        log.trace("sendNotification end");
    }
}
