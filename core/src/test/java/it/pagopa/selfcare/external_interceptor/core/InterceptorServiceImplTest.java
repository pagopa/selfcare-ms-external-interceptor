package it.pagopa.selfcare.external_interceptor.core;

import it.pagopa.selfcare.external_interceptor.connector.api.FDApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterceptorServiceImplTest {

    @InjectMocks
    private InterceptorServiceImpl serviceMock;
    @Mock
    private FDApiConnector fdApiConnectorMock;


    @Test
    void checkOrganization(){
        //given
        final String fiscalCode = "fiscalCode";
        final String vatNumber = "vatNumber";
        final OrganizationLightBean organizationLightBean = new OrganizationLightBean();
        organizationLightBean.setAlreadyRegistered(true);
        when(fdApiConnectorMock.checkOrganization(anyString(), anyString())).thenReturn(organizationLightBean);
        //when
        boolean isRegistered = serviceMock.checkOrganization(fiscalCode, vatNumber);
        //then
        assertTrue(isRegistered);
        verify(fdApiConnectorMock, times(1)).checkOrganization(fiscalCode, vatNumber);
        verifyNoMoreInteractions(fdApiConnectorMock);
    }

}