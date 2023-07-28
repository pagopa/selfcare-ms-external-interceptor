package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Scope("prototype")
@Service
public abstract class KafkaSend implements KafkaSendService {
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    NotificationMapper notificationMapper;
    @Autowired
    ObjectMapper mapper;

    KafkaSend(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    void sendNotification(String message, String topic, String successLog, String logFailure) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info(successLog);
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn(logFailure, ex.getMessage(), ex);
            }
        });
    }
}
