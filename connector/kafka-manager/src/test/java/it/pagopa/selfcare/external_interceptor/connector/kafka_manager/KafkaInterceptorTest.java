package it.pagopa.selfcare.external_interceptor.connector.kafka_manager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.selfcare.external_interceptor.connector.api.InternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config.KafkaConsumerConfig;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.config.KafkaProducerConfig;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Billing;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {KafkaInterceptor.class, KafkaConsumerConfig.class, KafkaProducerConfig.class})
@EmbeddedKafka(partitions = 1, controlledShutdown = true)
@DirtiesContext
@TestPropertySource(properties = {
        "external-interceptor.producer-topics={'prod-fd':'selfcare-fd'}",
        "kafka-manager.external-interceptor.read-topic=sc-contracts",
        "kafka-manager.external-interceptor.bootstrapAddress=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "kafka-manager.external-interceptor.auto-offset-reset=earliest",
        "spring.cloud.stream.kafka.binder.zkNodes=${spring.embedded.zookeeper.connect}",
        "kafka-manager.external-interceptor.groupId=consumer-test",
        "kafka-manager.external-interceptor.security-protocol=PLAINTEXT"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Profile("KafkaInterceptor")
@ContextConfiguration(classes = {KafkaInterceptorTest.Config.class})
class KafkaInterceptorTest {
    public static class Config {
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.registerModule(new Jdk8Module());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setTimeZone(TimeZone.getDefault());
            return mapper;
        }
    }
    @Autowired
    private ObjectMapper mapper;
    @SpyBean
    private KafkaInterceptor interceptor;
    @MockBean
    private InternalApiConnector apiConnector;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Captor
    ArgumentCaptor<ConsumerRecord<String, String>> notificationArgumentCaptor;

    private ListenableFuture mockFuture;
    private SendResult<String, String> mockSendResult;
    private Producer<String, String> producer;
    public KafkaInterceptorTest() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setTimeZone(TimeZone.getDefault());
    }


    @BeforeEach
    void setUp() throws InterruptedException {
        mockFuture = mock(ListenableFuture.class);
        mockSendResult = mock(SendResult.class);
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<String, String>(configs).createProducer();
        reset(interceptor, apiConnector);
        Thread.sleep(1000);
    }

    @Test
    void interceptKafkaMessage_Ok() throws JsonProcessingException {
        //given
        Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        ListenableFuture<SendResult<String, String>> future = mock(ListenableFuture.class);
        when(kafkaTemplate.send(any(), any())).thenReturn(future);
        doAnswer(invocationOnMock -> {
            ListenableFutureCallback callback = invocationOnMock.getArgument(0);
            callback.onSuccess(mockSendResult);
            return null;
        }).when(future).addCallback(any(ListenableFutureCallback.class));

        //when
        producer.send(new ProducerRecord<>("sc-contracts", mapper.writeValueAsString(notification)));
        producer.flush();
        //then
        verify(interceptor, timeout(1000).times(1))
                .intercept(notificationArgumentCaptor.capture());
    }
}