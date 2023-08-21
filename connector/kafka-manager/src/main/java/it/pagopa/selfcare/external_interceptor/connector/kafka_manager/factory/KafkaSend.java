package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Slf4j
@Scope("prototype")
@Service
public abstract class KafkaSend implements KafkaSendService {
    KafkaTemplate<String, String> kafkaTemplate;
    final NotificationMapper notificationMapper;
    final ObjectMapper mapper;
    final RegistryProxyConnector registryProxyConnector;

    KafkaSend(KafkaTemplate<String, String> kafkaTemplate, NotificationMapper notificationMapper, ObjectMapper mapper, RegistryProxyConnector registryProxyConnector){
        this.kafkaTemplate = kafkaTemplate;
        this.notificationMapper = notificationMapper;
        this.mapper = mapper;
        this.registryProxyConnector = registryProxyConnector;
    }

    void sendNotification(String message, String topic, String successLog, String logFailure, Acknowledgment acknowledgment) {
        log.trace("sendNotification start");
        log.debug("send notification message = {}, to topic: {}", message, topic);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info(successLog);
                acknowledgment.acknowledge();
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn(logFailure, ex.getMessage(), ex);
                acknowledgment.nack(60000);
            }
        });
        log.trace("sendNotification end");
    }
}
