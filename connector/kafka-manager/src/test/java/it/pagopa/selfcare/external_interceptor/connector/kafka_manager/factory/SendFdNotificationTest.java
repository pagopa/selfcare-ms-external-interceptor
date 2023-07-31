package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Billing;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotify;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ContextConfiguration(classes = {SendFdNotificationTest.Config.class, SendFdNotification.class, KafkaSend.class, NotificationMapperImpl.class})
@ExtendWith(MockitoExtension.class)
class SendFdNotificationTest {

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
    private Map<String, String> allowedTopics;
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ListenableFutureCallback<SendResult<String, String>> mockProducerCallback;
    private ListenableFuture mockFuture;
    private SendResult<String, String> mockSendResult;

    private SendFdNotification service;

    @Mock
    private KafkaSend abstractService;
    @Spy
    private NotificationMapper notificationMapperSpy = new NotificationMapperImpl();
    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ObjectMapper objectMapper;

    AutoCloseable closeable;
    @BeforeEach
    void setUp() {
       closeable = openMocks(this);
        allowedTopics = new HashMap<>();
        allowedTopics.put("prod-fd", "selfcare-fd");
        kafkaTemplate = mock(KafkaTemplate.class);
        mockFuture = mock(ListenableFuture.class);
        mockSendResult = mock(SendResult.class);
        service = new SendFdNotification(allowedTopics, kafkaTemplate, notificationMapperSpy, mapper);
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }
    @Test
    void sendInstitutionNotification(){
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");

        when(kafkaTemplate.send(any(), any()))
                .thenReturn(mockFuture);

        doAnswer(invocationOnMock -> {
            ListenableFutureCallback callback = invocationOnMock.getArgument(0);
            callback.onSuccess(mockSendResult);
            return null;
        }).when(mockFuture).addCallback(any(ListenableFutureCallback.class));

        //when
        Executable executable = () -> service.sendInstitutionNotification(notification);
        //then
        assertDoesNotThrow(executable);
        verify(kafkaTemplate, times(1)).send(eq(allowedTopics.get("prod-fd")), notNull());
    }

    @Test
    void sendUserNotification_ok(){
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(RelationshipState.ACTIVE);
        notification.setUser(userNotify);
        notification.setProductId("prod-fd");

        when(kafkaTemplate.send(any(), any()))
                .thenReturn(mockFuture);

        doAnswer(invocationOnMock -> {
            ListenableFutureCallback callback = invocationOnMock.getArgument(0);
            callback.onSuccess(mockSendResult);
            return null;
        }).when(mockFuture).addCallback(any(ListenableFutureCallback.class));
        //when
        Executable executable = () -> service.sendUserNotification(notification);
        //then
        assertDoesNotThrow(executable);
        verify(kafkaTemplate, times(1)).send(eq(allowedTopics.get("prod-fd")), notNull());
    }

    @Test
    void failed_send(){
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        RuntimeException ex = new RuntimeException("error");
        when(kafkaTemplate.send(any(), any()))
                .thenReturn(mockFuture);
        doAnswer(invocationOnMock -> {
            ListenableFutureCallback callback = invocationOnMock.getArgument(0);
            callback.onFailure(ex);
            return null;
        }).when(mockFuture).addCallback(any(ListenableFutureCallback.class));      //when
        assertDoesNotThrow(
                () -> service.sendInstitutionNotification(notification)
        );
        //then
        verify(kafkaTemplate, times(1)).send(eq(allowedTopics.get("prod-fd")), notNull());
    }
}