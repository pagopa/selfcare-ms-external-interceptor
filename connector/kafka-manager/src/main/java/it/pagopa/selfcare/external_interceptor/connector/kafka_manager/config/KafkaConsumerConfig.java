package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PropertySource("classpath:config/kafka-manager.properties")
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value(value = "${kafka-manager.onboarding-interceptor.clientId}")
    private String clientId;
    @Value(value = "${kafka-manager.onboarding-interceptor.groupId}")
    private String groupId;
    @Value(value = "${kafka-manager.onboarding-interceptor.bootstrapAddress}")
    private String bootstrapAddress;
    @Value(value = "${kafka-manager.onboarding-interceptor.auto-offset-reset}")
    private String autoOffsetReset;
    @Value(value = "${kafka-manager.onboarding-interceptor.security-protocol}")
    private String securityProtocol;
    @Value(value = "${kafka-manager.onboarding-interceptor.sasl-mechanism}")
    private String saslMechanism;
    @Value(value = "${kafka-manager.onboarding-interceptor.sasl-config}")
    private String saslConfig;
    @Value(value = "${kafka-manager.onboarding-interceptor.consumer-concurrency}")
    private int consumerConcurrency;
    @Value(value = "${kafka-manager.onboarding-interceptor.max-poll.records}")
    private int maxPollRecords;
    @Value(value = "${kafka-manager.onboarding-interceptor.interval}")
    private int maxPollInterval;
    @Value(value = "${kafka-manager.onboarding-interceptor.request-timeout-ms}")
    private int requestTimeOut;
    @Value(value = "${kafka-manager.onboarding-interceptor.session-timeout-ms}")
    private int sessionTimeOut;
    @Value(value = "${kafka-manager.onboarding-interceptor.connection-max-idle-ms}")
    private int connectionMaxIdleTimeOut;
    @Value(value = "${kafka-manager.onboarding-interceptor.metadata-max-age-ms}")
    private int metadataMaxAge;


    @Bean
    public ConsumerFactory<String, String> onboardedInstitutionConsumerFactory() {
        log.trace("Initializing {}", KafkaConsumerConfig.class.getSimpleName());
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeOut);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeOut);
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionMaxIdleTimeOut);
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAge);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslConfig);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(onboardedInstitutionConsumerFactory());
        factory.setConcurrency(consumerConcurrency);
        return factory;
    }


}
