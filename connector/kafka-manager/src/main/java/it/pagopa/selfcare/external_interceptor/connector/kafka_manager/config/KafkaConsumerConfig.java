package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config;

import lombok.Data;
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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PropertySource("classpath:config/kafka-manager.properties")
@Configuration
@EnableKafka
@Data
public class KafkaConsumerConfig {

    @Value(value = "${kafka-manager.external-interceptor.clientId}")
    private String clientId;
    @Value(value = "${kafka-manager.external-interceptor.contracts-groupId-general}")
    private String contractsGroupIdGeneral;
    @Value(value = "${kafka-manager.external-interceptor.contracts-groupId-sap}")
    private String contractsGroupIdSap;
    @Value(value = "${kafka-manager.external-interceptor.users-groupId}")
    private String usersGroupId;
    @Value(value = "${kafka-manager.external-interceptor.bootstrapAddress}")
    private String bootstrapAddress;
    @Value(value = "${kafka-manager.external-interceptor.auto-offset-reset}")
    private String autoOffsetReset;
    @Value(value = "${kafka-manager.external-interceptor.security-protocol}")
    private String securityProtocol;
    @Value(value = "${kafka-manager.external-interceptor.sasl-mechanism}")
    private String saslMechanism;
    @Value(value = "${kafka-manager.external-interceptor.sc-contracts-sasl-config}")
    private String saslScContractsConfig;
    @Value(value = "${kafka-manager.external-interceptor.sc-users-sasl-config}")
    private String saslScUsersConfig;
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

    public Map<String, Object> createConsumerForInstitutionTopicGeneral(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, contractsGroupIdGeneral);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId+"-institutions-general-local");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
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
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslScContractsConfig);
        return props;
    }
    public Map<String, Object> createConsumerForInstitutionTopicSap(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, contractsGroupIdSap);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId+"-institutions-sap-local");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
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
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslScContractsConfig);
        return props;
    }


    public Map<String, Object> createConsumerForUserTopic(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, usersGroupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId+"-users-local");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
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
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslScUsersConfig);
        return props;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaUserListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createConsumerForUserTopic()));
        factory.setConcurrency(consumerConcurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaContractsListenerContainerFactoryGeneral() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createConsumerForInstitutionTopicGeneral()));
        factory.setConcurrency(consumerConcurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaContractsListenerContainerFactorySap() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createConsumerForInstitutionTopicSap()));
        factory.setConcurrency(consumerConcurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


}
