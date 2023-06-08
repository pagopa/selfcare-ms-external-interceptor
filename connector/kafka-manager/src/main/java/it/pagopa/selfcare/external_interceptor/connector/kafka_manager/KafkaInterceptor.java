package it.pagopa.selfcare.external_interceptor.connector.kafka_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class KafkaInterceptor {

    @Autowired
    private ObjectMapper mapper;

    private final Optional<Map<String, String>> producerAllowedTopics;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaInterceptor(@Value("${external-interceptor.products-allowed-lis}")Map<String, String> producerAllowedTopics){
        this.producerAllowedTopics = Optional.ofNullable(producerAllowedTopics);
    }

    @KafkaListener(topics = "${kafka-manager.external-interceptor.read-topic}")
    public void intercept(ConsumerRecord<String, String> inboundRecord) {
        log.trace("KafkaInterceptor intercept start");
        log.debug("KafKaInterceptor incoming message = {}", inboundRecord);
        Notification notification = null;

        try {
            notification = mapper.readValue(inboundRecord.value(), Notification.class);
            if(producerAllowedTopics.get().containsKey(notification.getProduct())){
                NotificationToSend notificationToSend = createInstitutionNotification(notification);
                String message = mapper.writeValueAsString(notificationToSend);
                kafkaTemplate.send(producerAllowedTopics.get().get(notification.getProduct()), message);
            }
        } catch (Exception e) {
            log.warn("Something went wrong with message processing");
        }

    }
    
    private NotificationToSend createInstitutionNotification(Notification inbound){

        return null;
    }

    private NotificationToSend createUserNotification(Notification inbound, List<User> users){
        return null;
    }

}
