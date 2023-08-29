package it.pagopa.selfcare.external_interceptor.connector.rest.interceptor;

import feign.RequestTemplate;
import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.FDTokenRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.EncodedParamForm;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.auth.OauthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FDAuthorizationInterceptorTest {

    private FDAuthorizationInterceptor interceptor;
    @Mock
    private FDTokenRestClient tokenRestClient;
    private RequestTemplate template;

    @BeforeEach
    void setUp(){
        final String grantType = "grant_type";
        final String clientId = "client_id";
        final String clientSecret = "client_secret";
        interceptor = new FDAuthorizationInterceptor(tokenRestClient, grantType, clientId, clientSecret);
        template = new RequestTemplate();
    }

    @Test
    void apply(){
        //given
        OauthToken token = new OauthToken();
        token.setAccessToken("token");
        when(tokenRestClient.getFDToken(any())).thenReturn(token);
        //when
        interceptor.apply(template);
        //then
        ArgumentCaptor<EncodedParamForm> paramCaptor = ArgumentCaptor.forClass(EncodedParamForm.class);
        verify(tokenRestClient, times(1)).getFDToken(paramCaptor.capture());
        EncodedParamForm params = paramCaptor.getValue();
        TestUtils.checkNotNullFields(params);
        verifyNoMoreInteractions(tokenRestClient);
    }
}