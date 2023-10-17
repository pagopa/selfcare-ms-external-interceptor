package it.pagopa.selfcare.external_interceptor.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.api.KafkaSapSendService;
import it.pagopa.selfcare.external_interceptor.connector.api.MsCoreConnector;
import it.pagopa.selfcare.external_interceptor.connector.api.RegistryProxyConnector;
import it.pagopa.selfcare.external_interceptor.connector.exceptions.ResourceNotFoundException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Billing;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.InstitutionUpdate;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.OnboardedProduct;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import it.pagopa.selfcare.external_interceptor.core.config.ScheduledConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceImplTest {

    @InjectMocks
    private SchedulerServiceImpl schedulerService;
    @Mock
    private KafkaSapSendService sapSendService;
    @Mock
    private MsCoreConnector msCoreConnector;
    @Mock
    private RegistryProxyConnector registryProxyConnector;
    @Spy
    private NotificationMapper notificationMapper = new NotificationMapperImpl();
    @Mock
    private ScheduledConfig scheduledConfig;


    @Test
    void regenerateQueueNotification() throws JsonProcessingException {
        //given
        final Token token= mockInstance(new Token());
        token.setStatus(RelationshipState.ACTIVE);
        final InstitutionUpdate institutionUpdate = mockInstance(new InstitutionUpdate());
        token.setInstitutionUpdate(institutionUpdate);
        final Institution institution = mockInstance(new Institution());
        final OnboardedProduct onboardedProduct = mockInstance(new OnboardedProduct());
        final Billing billing = mockInstance(new Billing());
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        final String productId = "productId";

        schedulerService = new SchedulerServiceImpl(msCoreConnector, sapSendService, List.of(productId), scheduledConfig, notificationMapper, registryProxyConnector);

        final InstitutionProxyInfo institutionProxyInfo = mockInstance(new InstitutionProxyInfo());
        final GeographicTaxonomies geographicTaxonomies = mockInstance(new GeographicTaxonomies());

        when(msCoreConnector.getInstitutionById(anyString())).thenReturn(institution);
        when(scheduledConfig.getSendOldEvent()).thenReturn(true);
        when(msCoreConnector.retrieveTokensByProductId(anyString(), any(), any())).thenReturn(List.of(token));
        when(registryProxyConnector.getInstitutionProxyById(anyString())).thenReturn(institutionProxyInfo);
        when(registryProxyConnector.getExtById(anyString())).thenReturn(geographicTaxonomies);

        //when
        Executable executable = () -> schedulerService.regenerateQueueNotifications();
        //then
        assertDoesNotThrow(executable);
        verify(msCoreConnector, times(1)).retrieveTokensByProductId(productId, 0, 100
        );
        verify(msCoreConnector, times(1)).getInstitutionById(token.getInstitutionId());
        verify(registryProxyConnector, times(1)).getInstitutionProxyById(institution.getExternalId());
        verify(sapSendService, times(1)).sendOldEvents(any());
        verify(registryProxyConnector, times(1)).getExtById(institutionProxyInfo.getIstatCode());
    }

    @Test
    void resourceNotFound_proxy() throws JsonProcessingException {
        //given
        final Token token= mockInstance(new Token());
        final InstitutionUpdate institutionUpdate = mockInstance(new InstitutionUpdate());
        token.setInstitutionUpdate(institutionUpdate);
        final Institution institution = mockInstance(new Institution());
        final OnboardedProduct onboardedProduct = mockInstance(new OnboardedProduct());
        final Billing billing = mockInstance(new Billing());
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        final String productId = "productId";

        schedulerService = new SchedulerServiceImpl(msCoreConnector, sapSendService, List.of(productId), scheduledConfig, notificationMapper, registryProxyConnector);

        final InstitutionProxyInfo institutionProxyInfo = mockInstance(new InstitutionProxyInfo());
        final GeographicTaxonomies geographicTaxonomies = mockInstance(new GeographicTaxonomies());

        when(msCoreConnector.getInstitutionById(anyString())).thenReturn(institution);
        when(scheduledConfig.getSendOldEvent()).thenReturn(true);
        when(msCoreConnector.retrieveTokensByProductId(anyString(), any(), any())).thenReturn(List.of(token));
        when(registryProxyConnector.getInstitutionProxyById(anyString())).thenThrow(ResourceNotFoundException.class);

        //when
        Executable executable = () -> schedulerService.regenerateQueueNotifications();
        //then
        assertDoesNotThrow(executable);
        verify(msCoreConnector, times(1)).retrieveTokensByProductId(productId, 0, 100);
        verify(msCoreConnector, times(1)).getInstitutionById(token.getInstitutionId());
        verify(registryProxyConnector, times(1)).getInstitutionProxyById(institution.getExternalId());
        verify(sapSendService, times(1)).sendOldEvents(any());

    }

    @Test
    void resourceNotFound_msCore(){
        //given
        final Token token= mockInstance(new Token());
        final InstitutionUpdate institutionUpdate = mockInstance(new InstitutionUpdate());
        token.setInstitutionUpdate(institutionUpdate);
        final Institution institution = mockInstance(new Institution());
        final OnboardedProduct onboardedProduct = mockInstance(new OnboardedProduct());
        final Billing billing = mockInstance(new Billing());
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        final String productId = "productId";

        schedulerService = new SchedulerServiceImpl(msCoreConnector, sapSendService, List.of(productId), scheduledConfig, notificationMapper, registryProxyConnector);

        final InstitutionProxyInfo institutionProxyInfo = mockInstance(new InstitutionProxyInfo());
        final GeographicTaxonomies geographicTaxonomies = mockInstance(new GeographicTaxonomies());

        when(msCoreConnector.getInstitutionById(anyString())).thenThrow(ResourceNotFoundException.class);
        when(scheduledConfig.getSendOldEvent()).thenReturn(true);
        when(msCoreConnector.retrieveTokensByProductId(anyString(), any(), any())).thenReturn(List.of(token));

        //when
        Executable executable = () -> schedulerService.regenerateQueueNotifications();
        //then
        assertDoesNotThrow(executable);
        verifyNoInteractions(sapSendService, registryProxyConnector);
        verify(msCoreConnector, times(1)).retrieveTokensByProductId(productId, 0, 100);
        verify(msCoreConnector, times(1)).getInstitutionById(token.getInstitutionId());
    }
    @Test
    void sendSapNotification_notActive( ) throws JsonProcessingException {
        //given
        final Token token= mockInstance(new Token());
        token.setStatus(RelationshipState.PENDING);
        final InstitutionUpdate institutionUpdate = mockInstance(new InstitutionUpdate());
        token.setInstitutionUpdate(institutionUpdate);
        final Institution institution = mockInstance(new Institution());
        final OnboardedProduct onboardedProduct = mockInstance(new OnboardedProduct());
        final Billing billing = mockInstance(new Billing());
        onboardedProduct.setBilling(billing);
        institution.setOnboarding(List.of(onboardedProduct));
        final String productId = "productId";
        when(scheduledConfig.getSendOldEvent()).thenReturn(true);
        when(msCoreConnector.retrieveTokensByProductId(anyString(), any(), any())).thenReturn(List.of(token));
        schedulerService = new SchedulerServiceImpl(msCoreConnector, sapSendService, List.of(productId), scheduledConfig, notificationMapper, registryProxyConnector);
        //when
        Executable executable = () -> schedulerService.regenerateQueueNotifications();
        //then
        assertDoesNotThrow(executable);
        verify(msCoreConnector, times(1)).retrieveTokensByProductId(productId, 0, 100);
        verifyNoMoreInteractions(msCoreConnector);
        verifyNoInteractions( registryProxyConnector, notificationMapper, sapSendService);
    }

    @Test
    void startScheduler(){
        //when
        schedulerService.startScheduler(Optional.of(1));
        //then
        verify(scheduledConfig,times(1)).setScheduler(true);
    }

}