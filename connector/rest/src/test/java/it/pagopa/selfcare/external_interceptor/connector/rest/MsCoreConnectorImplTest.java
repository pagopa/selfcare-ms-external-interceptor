package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.MsCoreRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.MsCoreMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.MsCoreMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core.TokenResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core.TokensResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static it.pagopa.selfcare.commons.utils.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MsCoreConnectorImplTest {

    @InjectMocks
    private MsCoreConnectorImpl msCoreConnector;
    @Mock
    private MsCoreRestClient restClient;

    @Spy
    private MsCoreMapper mapper = new MsCoreMapperImpl();


    @Test
    void getTokensByProductId(){
        //given
        final TokenResponse token = mockInstance(new TokenResponse());
        final TokensResponse tokensResponse = new TokensResponse(List.of(token));
        final String productId = "productId";
        final Integer page = 0;
        final Integer size = 0;
        when(restClient.retrieveTokensByProduct(anyString(), any(), any())).thenReturn(tokensResponse);

        //when
        List<Token> tokens = msCoreConnector.retrieveTokensByProductId(productId, page, size);
        //then
        assertNotNull(tokens);
       reflectionEqualsByName(token, tokens.get(0));
       verify(restClient, times(1)).retrieveTokensByProduct(productId, page, size);
    }

    @Test
    void getInstitutionById(){
        //given
        final String institutionId = UUID.randomUUID().toString();
        final InstitutionResponse institutionResponse = mockInstance(new InstitutionResponse());
        when(restClient.getInstitutionById(anyString())).thenReturn(institutionResponse);
        //when
        Institution institution = msCoreConnector.getInstitutionById(institutionId);
        //then
        reflectionEqualsByName(institutionResponse,institution);
        verify(restClient, times(1)).getInstitutionById(institutionId);
    }
}