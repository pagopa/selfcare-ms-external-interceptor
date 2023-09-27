package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.model.user.UserProductDetails;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.ExternalApiBackEndRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiConnectorImplTest {

    @InjectMocks
    private ExternalApiConnectorImpl externalApiConnector;

    @Mock
    private ExternalApiBackEndRestClient restClient;


    @Test
    void getUserProductDetails() {
        //given
        String userId = UUID.randomUUID().toString();
        String productId = "productId";
        String institutionId = UUID.randomUUID().toString();
        UserProductDetails userProductDetails = mock(UserProductDetails.class);

        when(restClient.getUserProductDetails(anyString(), anyString(), anyString())).thenReturn(userProductDetails);
        //when
        UserProductDetails result = externalApiConnector.getUserOnboardedProductDetails(userId, institutionId, productId);
        assertNotNull(result);
        verify(restClient, times(1)).getUserProductDetails(userId, institutionId, productId);
        verifyNoMoreInteractions(restClient);
    }

}