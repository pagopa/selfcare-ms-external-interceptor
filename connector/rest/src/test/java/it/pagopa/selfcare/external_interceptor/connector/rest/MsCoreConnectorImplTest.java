package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.core.generated.openapi.v1.dto.InstitutionResponse;
import it.pagopa.selfcare.core.generated.openapi.v1.dto.TokenListResponse;
import it.pagopa.selfcare.core.generated.openapi.v1.dto.TokenResponse;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.MsCoreRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.MsCoreMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.MsCoreMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void getTokensByProductId() {
        //given
        final TokenResponse token = mockInstance(new TokenResponse());
        final TokenListResponse tokensResponse = new TokenListResponse(List.of(token));
        final String productId = "productId";
        final Integer page = 0;
        final Integer size = 0;
        when(restClient._findFromProductUsingGET1(anyString(), any(), any())).thenReturn(ResponseEntity.of(Optional.of(tokensResponse)));

        //when
        List<Token> tokens = msCoreConnector.retrieveTokensByProductId(productId, page, size);
        //then
        assertNotNull(tokens);
        assertEquals(token.getChecksum(), tokens.get(0).getChecksum());
        verify(restClient, times(1))._findFromProductUsingGET1(productId, page, size);
    }

    @Test
    void getInstitutionById() {
        //given
        final String institutionId = UUID.randomUUID().toString();
        InstitutionResponse institutionResponse = mockInstance(new InstitutionResponse());
        institutionResponse.setId(institutionId);
        when(restClient._retrieveInstitutionByIdUsingGET(anyString())).thenReturn(ResponseEntity.of(Optional.of(institutionResponse)));
        //when
        Institution institution = msCoreConnector.getInstitutionById(institutionId);
        //then
        assertEquals(institutionResponse.getId(), institution.getId());

        verify(restClient, times(1))._retrieveInstitutionByIdUsingGET(institutionId);
    }
}