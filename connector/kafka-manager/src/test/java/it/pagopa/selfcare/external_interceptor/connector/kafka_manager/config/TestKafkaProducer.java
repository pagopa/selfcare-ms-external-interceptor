package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestKafkaProducer {

    @Autowired
    private KafkaTemplate<String, NotificationToSend> kafkaTemplate;

    public void send(String topic, NotificationToSend payload) {
        log.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}
