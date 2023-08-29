package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.pagopa.selfcare.commons.utils.TestUtils.checkNotNullFields;
import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FDApiConnectorImplTest {


    @Mock
    private FDRestClient restClientMock;

    @Spy
    private FDMapper fdMapper = new FDMapperImpl();


    @Test
    void checkOrganization(){
        //given
        String fiscalCode = "fiscalCode";
        String vatNumber = "vatNumber";
        FDApiConnectorImpl fdApiConnectorMock = new FDApiConnectorImpl(fdMapper, restClientMock);
        OrganizationLightBeanResponse lightBeanResponseMock = mockInstance(new OrganizationLightBeanResponse());
        OrganizationResponse organizationMock = mockInstance(new OrganizationResponse());
        lightBeanResponseMock.setOrganization(organizationMock);
        when(restClientMock.checkOrganization(any(), any())).thenReturn(mockInstance(new OrganizationLightBeanResponse()));

        //when
        OrganizationLightBean result = fdApiConnectorMock.checkOrganization(fiscalCode, vatNumber);
        //then
        checkNotNullFields(result);
        verify(restClientMock, times(1)).checkOrganization(fiscalCode, vatNumber);
        verifyNoMoreInteractions(restClientMock);
    }
}