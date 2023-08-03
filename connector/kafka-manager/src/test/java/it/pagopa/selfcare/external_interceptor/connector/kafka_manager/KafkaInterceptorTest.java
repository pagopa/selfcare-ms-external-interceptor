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
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
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

    private KafkaInterceptor interceptor;
    private KafkaSendStrategyFactory sendStrategyFactory;

    private SendSapNotification sapSendService;

    private SendFdNotification fdNotificationService;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        openMocks(this);
        fdNotificationService = mock(SendFdNotification.class);
        sapSendService = mock(SendSapNotification.class);
        sendStrategyFactory = mock(KafkaSendStrategyFactory.class);
        interceptor = new KafkaInterceptor(mapper, sendStrategyFactory, sapSendService);
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
    void interceptInstitutionGeneral() throws JsonProcessingException {
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitutionGeneral(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", objectMapper.writeValueAsString(notification)), acknowledgment)
        );
        //then
        verify(sendStrategyFactory, times(1)).create("prod-fd");
        verify(fdNotificationService, times(1)).sendInstitutionNotification(notification, acknowledgment);
    }

    @Test
    void interceptInstitutionGeneralDifferentProduct() throws JsonProcessingException {
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-io");
        notification.setState("ACTIVE");
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        when(sendStrategyFactory.create(any())).thenReturn(null);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitutionGeneral(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", objectMapper.writeValueAsString(notification)), acknowledgment)
        );
        //then
        verify(sendStrategyFactory, times(1)).create("prod-io");
        verifyNoInteractions(fdNotificationService);
    }

    @Test
    void interceptInstitutionGeneral_exception() throws IOException {
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
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitutionGeneral(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", newPayload), acknowledgment)
        );
        //then
        verifyNoInteractions( fdNotificationService, sendStrategyFactory);
    }


    @Test
    void interceptUserNotification() throws JsonProcessingException {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(RelationshipState.ACTIVE);
        notification.setUser(userNotify);
        notification.setProductId("prod-fd");
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptUsers(new ConsumerRecord<>("sc-users", 0, 0, "notification", objectMapper.writeValueAsString(notification)), acknowledgment)
        );
        //then
        verify(sendStrategyFactory, times(1)).create("prod-fd");
        verify(fdNotificationService, times(1)).sendUserNotification(notification, acknowledgment);
    }

    @Test
    void interceptUser_exception(){
        //given
        final Notification notification = mockInstance(new Notification());
        notification.setProduct("prod-fd");
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        when(sendStrategyFactory.create(any())).thenReturn(fdNotificationService);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptUsers(new ConsumerRecord<>("sc-users", 0, 0, "notification", objectMapper.writeValueAsString(notification)), acknowledgment)
        );
        //then
        verifyNoInteractions(sendStrategyFactory, fdNotificationService);
    }

    @Test
    void interceptUserDifferentProd() throws JsonProcessingException {
        //given
        final UserNotification notification = mockInstance(new UserNotification());
        final UserNotify userNotify = mockInstance(new UserNotify());
        userNotify.setRelationshipStatus(RelationshipState.ACTIVE);
        notification.setUser(userNotify);
        notification.setProductId("prod-io");
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        when(sendStrategyFactory.create(any())).thenReturn(null);
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptUsers(new ConsumerRecord<>("sc-users", 0, 0, "notification", objectMapper.writeValueAsString(notification)),acknowledgment)
        );
        //then
        verify(sendStrategyFactory, times(1)).create("prod-io");
        verifyNoInteractions(fdNotificationService);
    }

    @Test
    void interceptInstitutionSap() throws JsonProcessingException {
        //given
        final Notification notification = mockInstance(new Notification());
        Institution institution = mockInstance(new Institution());
        final Billing billing = mockInstance(new Billing());
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-fd");
        notification.setState("ACTIVE");
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitutionSap(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", objectMapper.writeValueAsString(notification)), acknowledgment)
        );
        //then
        verify(sapSendService, times(1)).sendInstitutionNotification(notification, acknowledgment);
    }

    @Test
    void interceptInstitutionSap_exception() throws IOException {
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
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        //when
        assertDoesNotThrow(
                () -> interceptor.interceptInstitutionSap(new ConsumerRecord<>("sc-Contracts", 0, 0, "notification", newPayload), acknowledgment)
        );
        //then
        verifyNoInteractions( fdNotificationService, sendStrategyFactory);
    }
}
