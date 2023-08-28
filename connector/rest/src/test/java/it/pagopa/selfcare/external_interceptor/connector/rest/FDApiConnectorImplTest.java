package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.FDMapperImpl;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.TokenMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.TokenMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FDApiConnectorImplTest {

    @InjectMocks
    private FDApiConnectorImpl fdApiConnectorMock;
    @Mock
    private FDRestClient restClientMock;

    @Spy
    private FDMapper fdMapper = new FDMapperImpl();

    @Spy
    private TokenMapper tokenMapper = new TokenMapperImpl();

    @Test
    void checkOrganization(){



    }
}