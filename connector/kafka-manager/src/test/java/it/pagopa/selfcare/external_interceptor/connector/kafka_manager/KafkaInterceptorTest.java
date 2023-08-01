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
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.KafkaSendStrategyFactory;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.SendFdNotification;
import it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.SendSapNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Billing;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotify;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

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

    @Mock
    private ListenableFutureCallback<SendResult<String, String>> mockProducerCallback;
    private KafkaInterceptor interceptor;
    private InternalApiConnector apiConnector;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ListenableFuture mockFuture;
    private KafkaSendStrategyFactory sendStrategyFactory;

    private SendSapNotification sapSendService;

    private SendFdNotification fdNotificationService;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ObjectMapper objectMapper;
    private Map<String, String> allowedTopics;
    private SendResult<String, String> mockSendResult;

    @BeforeEach
    void setUp() {
        openMocks(this);
        allowedTopics = new HashMap<>();
        allowedTopics.put("prod-fd", "selfcare-fd");
        kafkaTemplate = mock(KafkaTemplate.class);
        mockFuture = mock(ListenableFuture.class);
        fdNotificationService = mock(SendFdNotification.class);
        sapSendService = mock(SendSapNotification.class);
        sendStrategyFactory = mock(KafkaSendStrategyFactory.class);
        interceptor = new KafkaInterceptor(mapper, sendStrategyFactory, sapSendService);
        mockSendResult = mock(SendResult.class);
    }
    public KafkaInterceptorTest(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setTimeZone(TimeZone.getDefault());
    }


    @Test
    void interceptInstitution() throws JsonProcessingException {
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitution(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", objectMapper.writeValueAsString(notification)))
        );
        //then
        verify(sendStrategyFactory, times(1)).create("prod-fd");
        verify(sapSendService, times(1)).sendInstitutionNotification(notification);
        verify(fdNotificationService, times(1)).sendInstitutionNotification(notification);
    }

    @Test
    void interceptInstitution_exception() throws IOException {
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        String notificationString = objectMapper.writeValueAsString(notification);
        String newPayload = notificationString.replace("PA", "TEST");
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitution(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", newPayload))
        );
        //then
        verifyNoInteractions(sapSendService, fdNotificationService, sendStrategyFactory);
    }


    @Test
    void interceptUserNotification() throws JsonProcessingException {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(RelationshipState.ACTIVE);
        notification.setUser(userNotify);
        notification.setProductId("prod-fd");

        when(kafkaTemplate.send(any(), any()))
                .thenReturn(mockFuture);
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptUsers(new ConsumerRecord<>("sc-users", 0, 0, "notification", objectMapper.writeValueAsString(notification)))
        );
        //then
        verify(sendStrategyFactory, times(1)).create("prod-fd");
        verify(fdNotificationService, times(1)).sendUserNotification(notification);
    }

    @Test
    void interceptUser_exception(){
        //given
        final Notification notification = mockInstance(new Notification());
        notification.setProduct("prod-fd");

        when(kafkaTemplate.send(any(), any()))
                .thenReturn(mockFuture);
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptUsers(new ConsumerRecord<>("sc-users", 0, 0, "notification", objectMapper.writeValueAsString(notification)))
        );
        //then
        verifyNoInteractions(sendStrategyFactory, fdNotificationService);
    }

}
