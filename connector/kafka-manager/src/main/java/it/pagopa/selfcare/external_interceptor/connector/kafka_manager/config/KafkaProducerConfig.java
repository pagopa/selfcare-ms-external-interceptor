package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Value(value = "${kafka-manager.external-interceptor.security-protocol}")
    private String securityProtocol;
    @Value(value = "${kafka-manager.external-interceptor.sasl-mechanism}")
    private String saslMechanism;
    @Value(value = "${kafka-manager.external-interceptor.sasl-producer-config-fd}")
    private String saslProducerConfigFd;
    @Value(value = "${kafka-manager.external-interceptor.sasl-producer-config-sap}")
    private String saslProducerConfigSap;

    @Bean
    public ProducerFactory<String, String> producerFactoryFd() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId+"-fd-producer");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslProducerConfigFd);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
    @Bean
    public ProducerFactory<String, String> producerFactorySap() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId+"-sap-producer");
        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslProducerConfigSap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    @Qualifier("fdProducer")
    public KafkaTemplate<String, String> kafkaTemplateFd() {
        return new KafkaTemplate<>(producerFactoryFd());
    }

    @Bean
    @Qualifier("sapProducer")
    public KafkaTemplate<String, String> kafkaTemplateSap(){
        return new KafkaTemplate<>(producerFactorySap());
    }
}
