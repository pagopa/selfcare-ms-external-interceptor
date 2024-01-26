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
import it.pagopa.selfcare.commons.base.utils.InstitutionType;
import it.pagopa.selfcare.commons.base.utils.Origin;
import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.api.RegistryProxyConnector;
import it.pagopa.selfcare.external_interceptor.connector.exceptions.ResourceNotFoundException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.*;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static it.pagopa.selfcare.commons.utils.TestUtils.checkNotNullFields;
import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory.KafkaSend.SCHEMA_VERSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ContextConfiguration(classes = {SendSapNotificationTest.Config.class, SendSapNotification.class, KafkaSend.class, NotificationMapperImpl.class})
@ExtendWith(MockitoExtension.class)
class SendSapNotificationTest {
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

    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private KafkaSend abstractService;
    @Spy
    private NotificationMapper notificationMapperSpy = new NotificationMapperImpl();
    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    private Acknowledgment acknowledgment;
    private ListenableFuture mockFuture;
    private SendResult<String, String> mockSendResult;
    private SendSapNotification service;

    private RegistryProxyConnector registryProxyConnector;
    private ExternalApiConnector externalApiConnector;
    AutoCloseable closeable;

    @Mock
    private ListenableFutureCallback<SendResult<String, String>> mockProducerCallback;


    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
        acknowledgment = mock(Acknowledgment.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        mockFuture = mock(ListenableFuture.class);
        mockSendResult = mock(SendResult.class);
        registryProxyConnector = mock(RegistryProxyConnector.class);
        externalApiConnector = mock(ExternalApiConnector.class);
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, Set.of(InstitutionType.PA), List.of("prod-io-premium", "prod-pn"), Set.of(Origin.IPA, Origin.SELC));
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    void sendInstitutionNotificationEc() throws JsonProcessingException {
        //given
        final Notification notification = createNotificationMock();
        Institution institution = mockInstance(new Institution(), "setCity", "rootParent");
        institution.setSubUnitType("EC");
        institution.setOrigin("IPA");
        institution.setInstitutionType(InstitutionType.PA);
        final Billing billing = createBillingMock();
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-io-premium");
        notification.setState("ACTIVE");
        InstitutionProxyInfo mockProxyInfo = mockInstance(new InstitutionProxyInfo());
        GeographicTaxonomies proxyTaxonomy = mockInstance(new GeographicTaxonomies());
        proxyTaxonomy.setCountry("proxyContry");
        proxyTaxonomy.setProvinceAbbreviation("proxyProvince");
        proxyTaxonomy.setDescription("proxyCity - COMUNE");
        proxyTaxonomy.setIstatCode(mockProxyInfo.getIstatCode());
        when(registryProxyConnector.getInstitutionProxyById(any())).thenReturn(mockProxyInfo);
        when(registryProxyConnector.getExtById(any())).thenReturn(proxyTaxonomy);
        when(kafkaTemplate.send(any(ProducerRecord.class)))
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
        ArgumentCaptor<ProducerRecord<String, String>> producerRecordArgumentCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(producerRecordArgumentCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();
        verify(registryProxyConnector, times(1)).getInstitutionProxyById(institution.getTaxCode());
        verify(registryProxyConnector, times(1)).getExtById(mockProxyInfo.getIstatCode());
        verifyNoMoreInteractions(registryProxyConnector);
        NotificationToSend captured = mapper.readValue(producerRecordArgumentCaptor.getValue().value(), NotificationToSend.class);
        checkNotNullFields(captured, "user");
        checkNotNullFields(captured.getInstitution());
    }

    @Test
    void sendInstitutionNotificationUo() throws JsonProcessingException {
        //given
        final Notification notification = createNotificationMock();
        Institution institution = mockInstance(new Institution(), "setCity");
        institution.setSubUnitType("UO");
        institution.setOrigin("IPA");
        institution.setInstitutionType(InstitutionType.PA);
        final Billing billing = createBillingMock();
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-pn");
        notification.setState("ACTIVE");
        OrganizationUnit mockUO = mockInstance(new OrganizationUnit());
        GeographicTaxonomies uoGeoTaxonomy = mockInstance(new GeographicTaxonomies());
        uoGeoTaxonomy.setCountry("uoCountry");
        uoGeoTaxonomy.setProvinceAbbreviation("uoProvince");
        uoGeoTaxonomy.setDescription("uoCity - COMUNE");
        uoGeoTaxonomy.setIstatCode(mockUO.getMunicipalIstatCode());
        when(registryProxyConnector.getUoById(any())).thenReturn(mockUO);
        when(registryProxyConnector.getExtById(any())).thenReturn(uoGeoTaxonomy);
        when(kafkaTemplate.send(any(ProducerRecord.class)))
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
        ArgumentCaptor<ProducerRecord<String, String>> producerRecordArgumentCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(producerRecordArgumentCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();
        verify(registryProxyConnector, times(1)).getUoById(institution.getSubUnitCode());
        verify(registryProxyConnector, times(1)).getExtById(mockUO.getMunicipalIstatCode());
        verifyNoMoreInteractions(registryProxyConnector);
        NotificationToSend captured = mapper.readValue(producerRecordArgumentCaptor.getValue().value(), NotificationToSend.class);
        checkNotNullFields(captured, "user");
        checkNotNullFields(captured.getInstitution());
    }

    @Test
    void sendInstitutionNotificationAoo() throws JsonProcessingException {
        //given
        final Notification notification = createNotificationMock();
        Institution institution = mockInstance(new Institution(), "setCity");
        institution.setSubUnitType("AOO");
        institution.setOrigin("IPA");
        institution.setInstitutionType(InstitutionType.PA);
        final Billing billing = createBillingMock();
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-pn");
        notification.setState("ACTIVE");
        HomogeneousOrganizationalArea mockUO = mockInstance(new HomogeneousOrganizationalArea());
        GeographicTaxonomies uoGeoTaxonomy = mockInstance(new GeographicTaxonomies());
        uoGeoTaxonomy.setCountry("aooCountry");
        uoGeoTaxonomy.setProvinceAbbreviation("aooProvince");
        uoGeoTaxonomy.setDescription("aooCity - COMUNE");
        uoGeoTaxonomy.setIstatCode(mockUO.getMunicipalIstatCode());
        when(registryProxyConnector.getAooById(any())).thenReturn(mockUO);
        when(registryProxyConnector.getExtById(any())).thenReturn(uoGeoTaxonomy);
        when(kafkaTemplate.send(any(ProducerRecord.class)))
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
        ArgumentCaptor<ProducerRecord<String, String>> producerRecordArgumentCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(producerRecordArgumentCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();
        verify(registryProxyConnector, times(1)).getAooById(institution.getSubUnitCode());
        verify(registryProxyConnector, times(1)).getExtById(mockUO.getMunicipalIstatCode());
        verifyNoMoreInteractions(registryProxyConnector);
        NotificationToSend captured = mapper.readValue(producerRecordArgumentCaptor.getValue().value(), NotificationToSend.class);
        checkNotNullFields(captured, "user");
        assertEquals("Sc-Contracts-Sap",producerRecordArgumentCaptor.getValue().topic());
        checkNotNullFields(captured.getInstitution());
    }

    @Test
    void sendInstitutionNotification_NotFound() throws JsonProcessingException {
        //given
        final Notification notification = createNotificationMock();
        Institution institution = mockInstance(new Institution(), "setCity");
        institution.setSubUnitType("AOO");
        institution.setOrigin("IPA");
        institution.setInstitutionType(InstitutionType.PA);
        final Billing billing = createBillingMock();
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-pn");
        notification.setState("ACTIVE");
        when(registryProxyConnector.getAooById(any())).thenThrow(ResourceNotFoundException.class);
        when(kafkaTemplate.send(any(ProducerRecord.class)))
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
        ArgumentCaptor<ProducerRecord<String, String>> producerRecordArgumentCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(producerRecordArgumentCaptor.capture());;
        verify(acknowledgment, times(1)).acknowledge();
        verify(registryProxyConnector, times(1)).getAooById(institution.getSubUnitCode());
        verifyNoMoreInteractions(registryProxyConnector);
        NotificationToSend captured = mapper.readValue(producerRecordArgumentCaptor.getValue().value(), NotificationToSend.class);
        checkNotNullFields(captured, "user");
        checkNotNullFields(captured.getInstitution(), "istatCode", "city", "country", "county");
    }

    @Test
    void sendSapNotification_nullSubUnitType() throws IOException {
        //given
        final Notification notification = createNotificationMock();
        Institution institution = createInstitutionMock();
        institution.setSubUnitType(null);
        institution.setOrigin("IPA");
        institution.setInstitutionType(InstitutionType.PA);
        final Billing billing = createBillingMock();
        notification.setInstitution(institution);
        notification.setBilling(billing);
        notification.setProduct("prod-pn");
        notification.setState("ACTIVE");
        when(kafkaTemplate.send(any(ProducerRecord.class)))
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
        ArgumentCaptor<ProducerRecord<String, String>> recordArgumentCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(recordArgumentCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();
        verifyNoInteractions(registryProxyConnector);
        ProducerRecord<String, String> captured = recordArgumentCaptor.getValue();
        assertNull(captured.headers().lastHeader(SCHEMA_VERSION));
    }

    @Test
    void noExcludedInstitutionTypes() {
        //given
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, null, List.of("prod-pn"), Set.of(Origin.IPA));
        final Notification notification = createNotificationMock();
        final Institution institution = createInstitutionMock();
        final OnboardedProduct onboardedProduct = createOnboardedProductMock();
        final Billing billing = createBillingMock();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setProduct("prod-pn");
        notification.setInstitution(institution);
        notification.setState("ACTIVE");

        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void allowedProductsNotPresent() {
        //given
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, Set.of(InstitutionType.PA), null, Set.of(Origin.IPA));
        final Notification notification = createNotificationMock();
        final Institution institution = createInstitutionMock();
        final OnboardedProduct onboardedProduct = createOnboardedProductMock();
        final Billing billing = createBillingMock();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        institution.setInstitutionType(InstitutionType.PA);
        notification.setInstitution(institution);
        notification.setState("ACTIVE");

        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void notificationInstitutionType_notPresent() {
        //given
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, Set.of(InstitutionType.PA), List.of("prod-pn"), Set.of(Origin.IPA));
        final Notification notification = createNotificationMock();
        final Institution institution = createInstitutionMock();
        institution.setInstitutionType(InstitutionType.SA);
        final OnboardedProduct onboardedProduct = createOnboardedProductMock();
        final Billing billing = createBillingMock();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setInstitution(institution);
        notification.setState("ACTIVE");
        notification.setProduct("prod-pn");
        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void productNotAllowed() {
        //given
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, Set.of(InstitutionType.PA), List.of("prod-pn"), Set.of(Origin.IPA));
        final Notification notification = createNotificationMock();
        final Institution institution = createInstitutionMock();
        institution.setInstitutionType(InstitutionType.SA);
        final OnboardedProduct onboardedProduct = createOnboardedProductMock();
        final Billing billing = createBillingMock();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setInstitution(institution);
        notification.setState("ACTIVE");
        notification.setProduct("prod-io");
        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void isProdIoFast(){
        //given
        final Notification notification = createNotificationMock();
        notification.setPricingPlan("FA");
        final Institution institution = createInstitutionMock();
        institution.setInstitutionType(InstitutionType.SA);
        final OnboardedProduct onboardedProduct = createOnboardedProductMock();
        final Billing billing = createBillingMock();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setInstitution(institution);
        notification.setState("ACTIVE");
        notification.setProduct("prod-io");
        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }
    @Test
    void allowedOriginsNotPresent() {
        //given
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, Set.of(InstitutionType.PA), List.of("prod-pn"), null);
        final Notification notification = new Notification();
        final Institution institution = new Institution();
        institution.setInstitutionType(InstitutionType.PA);
        institution.setOrigin(Origin.IPA.getValue());
        final OnboardedProduct onboardedProduct = new OnboardedProduct();
        final Billing billing = new Billing();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setInstitution(institution);
        notification.setState("ACTIVE");
        notification.setProduct("prod-pn");
        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void originNotAllowed(){
        //given
        service = new SendSapNotification(kafkaTemplate, notificationMapperSpy, mapper, registryProxyConnector, externalApiConnector, Set.of(InstitutionType.PA, InstitutionType.GSP), List.of("prod-pn"), Set.of(Origin.IPA));
        final Notification notification = new Notification();
        final Institution institution = new Institution();
        institution.setInstitutionType(InstitutionType.GSP);
        institution.setOrigin(Origin.SELC.getValue());
        final OnboardedProduct onboardedProduct = new OnboardedProduct();
        final Billing billing = new Billing();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setInstitution(institution);
        notification.setState("ACTIVE");
        notification.setProduct("prod-pn");
        //when
        Executable executable = () -> service.sendInstitutionNotification(notification, acknowledgment);
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void sendOldEvents() throws JsonProcessingException {
        //given
        final Notification notification = createNotificationMock();
        final Institution institution = createInstitutionMock();
        institution.setOrigin("IPA");
        institution.setInstitutionType(InstitutionType.PA);
        final OnboardedProduct onboardedProduct = createOnboardedProductMock();
        final Billing billing = createBillingMock();
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        notification.setInstitution(institution);
        notification.setState("ACTIVE");
        notification.setProduct("prod-pn");
        when(kafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(mockFuture);

        doAnswer(invocationOnMock -> {
            ListenableFutureCallback callback = invocationOnMock.getArgument(0);
            callback.onSuccess(mockSendResult);
            return null;
        }).when(mockFuture).addCallback(any(ListenableFutureCallback.class));

        //when
        Executable executable = () -> service.sendOldEvents(notification);
        //then
        assertDoesNotThrow(executable);
        ArgumentCaptor<ProducerRecord<String, String>> producerRecordArgumentCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(producerRecordArgumentCaptor.capture());
        verifyNoMoreInteractions(registryProxyConnector);
        NotificationToSend captured = mapper.readValue(producerRecordArgumentCaptor.getValue().value(), NotificationToSend.class);
        checkNotNullFields(captured, "user");
        checkNotNullFields(captured.getInstitution(), "subUnitType");
    }


    private static Notification createNotificationMock(){
        return mockInstance(new Notification());
    }

    private static Institution createInstitutionMock(){
        return mockInstance(new Institution());
    }

    private static Billing createBillingMock(){
        return mockInstance(new Billing());
    }

    private static OnboardedProduct createOnboardedProductMock(){
        return mockInstance(new OnboardedProduct());
    }
}