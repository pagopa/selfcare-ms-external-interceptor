package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.pagopa.selfcare.commons.utils.TestUtils.checkNotNullFields;
import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String grantType = "grant_type";
        String clientId = "client_id";
        String clientSecret = "client_secret";
        String fiscalCode = "fiscalCode";
        String vatNumber = "vatNumber";
        FDApiConnectorImpl fdApiConnectorMock = new FDApiConnectorImpl(fdMapper, restClientMock, grantType, clientId, clientSecret);
        OrganizationLightBeanResponse lightBeanResponseMock = mockInstance(new OrganizationLightBeanResponse());
        OrganizationResponse organizationMock = mockInstance(new OrganizationResponse());
        lightBeanResponseMock.setOrganization(organizationMock);
        OauthToken token = mockInstance(new OauthToken());
        when(restClientMock.getFDToken(any())).thenReturn(token);
        when(restClientMock.checkOrganization(any(), any())).thenReturn(mockInstance(new OrganizationLightBeanResponse()));

        //when
        OrganizationLightBean result = fdApiConnectorMock.checkOrganization(fiscalCode, vatNumber);
        //then
        ArgumentCaptor<EncodedParamForm> paramCaptor = ArgumentCaptor.forClass(EncodedParamForm.class);
        verify(restClientMock, times(1)).getFDToken(paramCaptor.capture());
        EncodedParamForm param = paramCaptor.getValue();
        assertEquals(grantType, param.getGrant_type());
        assertEquals(clientSecret, param.getClient_secret());
        assertEquals(clientId, param.getClient_id());
        checkNotNullFields(result);
        verify(restClientMock, times(1)).checkOrganization(fiscalCode, vatNumber);
        verifyNoMoreInteractions(restClientMock);
    }
}