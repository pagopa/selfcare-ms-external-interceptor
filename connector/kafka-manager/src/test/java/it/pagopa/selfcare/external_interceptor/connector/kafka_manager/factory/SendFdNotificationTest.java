package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Billing;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.QueueEvent;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.model.user.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static it.pagopa.selfcare.commons.utils.TestUtils.checkNotNullFields;
import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private Acknowledgment acknowledgment;
    private ExternalApiConnector externalApiConnector;

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
        acknowledgment = mock(Acknowledgment.class);
        mockSendResult = mock(SendResult.class);
        externalApiConnector = mock(ExternalApiConnector.class);
        service = new SendFdNotification(allowedTopics, kafkaTemplate, notificationMapperSpy, mapper, externalApiConnector);
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    void sendInstitutionNotification() throws JsonProcessingException {
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
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        ArgumentCaptor<String> institutionCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq(allowedTopics.get("prod-fd")), institutionCaptor.capture());
        NotificationToSend captured = mapper.readValue(institutionCaptor.getValue(), NotificationToSend.class);
        verify(acknowledgment, times(1)).acknowledge();
        checkNotNullFields(captured, "user");
        checkNotNullFields(captured.getInstitution());
    }

    @Test
    void sendUserNotification_ok() throws JsonProcessingException {
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
        Executable executable = () -> service.sendUserNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq(allowedTopics.get("prod-fd")), userCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();
        NotificationToSend captured = mapper.readValue(userCaptor.getValue(), NotificationToSend.class);
        checkNotNullFields(captured, "institution", "billing", "state", "closedAt", "fileName", "contentType");
    }

    @Test
    void failed_send() {
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
                () -> service.sendInstitutionNotification(notification, acknowledgment)
        );
        //then
        verify(kafkaTemplate, times(1)).send(eq(allowedTopics.get("prod-fd")), anyString());
        verify(acknowledgment, times(1)).nack(60000);
    }

    @Test
    void productNotAllowed() {
        //given
        Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-io");
        notification.setState("ACTIVE");
        //when
        assertDoesNotThrow(
                () -> service.sendInstitutionNotification(notification, acknowledgment)
        );
        //then
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void productMap_isEmpty() {
        //given
        Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        allowedTopics = null;
        service = new SendFdNotification(allowedTopics, kafkaTemplate, notificationMapperSpy, mapper, externalApiConnector);
        //when
        assertDoesNotThrow(
                () -> service.sendInstitutionNotification(notification, acknowledgment)
        );
        //then
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void productNotAllowed_users() {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(RelationshipState.ACTIVE);
        notification.setUser(userNotify);
        notification.setProductId("prod-io");
        //when
        assertDoesNotThrow(
                () -> service.sendUserNotification(notification, acknowledgment)
        );
        //then
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void productMap_isEmpty_users() {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(RelationshipState.ACTIVE);
        notification.setUser(userNotify);
        notification.setProductId("prod-fd");
        allowedTopics = null;
        service = new SendFdNotification(allowedTopics, kafkaTemplate, notificationMapperSpy, mapper, externalApiConnector);
        //when
        assertDoesNotThrow(
                () -> service.sendUserNotification(notification, acknowledgment)
        );
        //then
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void updateUserEvents() throws JsonProcessingException {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        notification.setEventType(QueueEvent.UPDATE);
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(null);
        notification.setUser(userNotify);
        notification.setProductId("prod-fd");
        UserProductDetails userProductDetails = mockInstance(new UserProductDetails());
        OnboardedProduct onboardedProduct = mockInstance(new OnboardedProduct());
        userProductDetails.setOnboardedProductDetails(onboardedProduct);

        when(externalApiConnector.getUserOnboardedProductDetails(anyString(), anyString(), anyString())).thenReturn(userProductDetails);
        when(kafkaTemplate.send(any(), any()))
                .thenReturn(mockFuture);

        doAnswer(invocationOnMock -> {
            ListenableFutureCallback callback = invocationOnMock.getArgument(0);
            callback.onSuccess(mockSendResult);
            return null;
        }).when(mockFuture).addCallback(any(ListenableFutureCallback.class));
        //when
        Executable executable = () -> service.sendUserNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        verify(externalApiConnector, times(1)).getUserOnboardedProductDetails(userNotify.getUserId(), notification.getInstitutionId(), notification.getProductId());
        verify(kafkaTemplate, times(2)).send(eq(allowedTopics.get("prod-fd")), userCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();
        NotificationToSend captured = mapper.readValue(userCaptor.getValue(), NotificationToSend.class);
        checkNotNullFields(captured, "institution", "billing", "state", "closedAt", "fileName", "contentType");
    }

    @Test
    void updateUserEvent_JsonException() throws JsonProcessingException {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        notification.setEventType(QueueEvent.UPDATE);
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(null);
        notification.setUser(userNotify);
        notification.setProductId("prod-fd");
        UserProductDetails userProductDetails = mockInstance(new UserProductDetails());
        OnboardedProduct onboardedProduct = mockInstance(new OnboardedProduct());
        userProductDetails.setOnboardedProductDetails(onboardedProduct);

        when(externalApiConnector.getUserOnboardedProductDetails(anyString(), anyString(), anyString())).thenReturn(userProductDetails);

        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        //when
        Executable executable = () -> service.sendUserNotification(notification, acknowledgment);
        //then
        assertThrows(RuntimeException.class, executable);
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        verify(externalApiConnector, times(1)).getUserOnboardedProductDetails(userNotify.getUserId(), notification.getInstitutionId(), notification.getProductId());
    }

}