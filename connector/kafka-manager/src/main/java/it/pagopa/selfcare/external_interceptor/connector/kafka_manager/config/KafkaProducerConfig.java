package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PropertySource("classpath:config/kafka-manager.properties")
@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value(value = "${kafka-manager.external-interceptor.clientId}")
    private String clientId;
    @Value(value = "${kafka-manager.external-interceptor.bootstrapAddress}")
    private String bootstrapAddress;
    @Value(value = "${kafka-manager.external-interceptor.auto-offset-reset}")
    private String autoOffsetReset;
    @Value(value = "${kafka-manager.external-interceptor.security-protocol}")
    private String securityProtocol;
    @Value(value = "${kafka-manager.external-interceptor.sasl-mechanism}")
    private String saslMechanism;
    @Value(value = "${kafka-manager.external-interceptor.sasl--producer-config}")
    private String saslProducerConfig;
    @Value(value = "${kafka-manager.external-interceptor.consumer-concurrency}")
    private int consumerConcurrency;
    @Value(value = "${kafka-manager.external-interceptor.max-poll.records}")
    private int maxPollRecords;
    @Value(value = "${kafka-manager.external-interceptor.interval}")
    private int maxPollInterval;
    @Value(value = "${kafka-manager.external-interceptor.request-timeout-ms}")
    private int requestTimeOut;
    @Value(value = "${kafka-manager.external-interceptor.session-timeout-ms}")
    private int sessionTimeOut;
    @Value(value = "${kafka-manager.external-interceptor.connection-max-idle-ms}")
    private int connectionMaxIdleTimeOut;
    @Value(value = "${kafka-manager.external-interceptor.metadata-max-age-ms}")
    private int metadataMaxAge;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslProducerConfig);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
