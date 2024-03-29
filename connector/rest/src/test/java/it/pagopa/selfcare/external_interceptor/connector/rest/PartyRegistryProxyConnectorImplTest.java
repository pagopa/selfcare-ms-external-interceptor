package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.*;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.PartyRegistryProxyRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.AooResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.GeographicTaxonomiesResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ProxyInstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UoResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.RegistryProxyMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.RegistryProxyMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.pagopa.selfcare.commons.utils.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PartyRegistryProxyConnectorImplTest {
    @InjectMocks
    private PartyRegistryProxyConnectorImpl partyRegistryProxyConnectorImpl;

    @Mock
    private PartyRegistryProxyRestClient partyRegistryProxyRestClient;

    @Spy
    RegistryProxyMapper registryProxyMapper = new RegistryProxyMapperImpl();

    private final static AooResponse aooResponse;
    private final static UoResponse uoResponse;

    static {
        aooResponse = new AooResponse();
        aooResponse.setCodAoo("codAoo");
        aooResponse.setId("id");
        aooResponse.setOrigin(Origin.IPA);

        uoResponse = new UoResponse();
        uoResponse.setUniUoCode("codiceUniUo");
        uoResponse.setId("id");
        uoResponse.setOrigin(Origin.IPA);

    }


    @Test
    void getInstitutionProxyInfo(){
        //given
        ProxyInstitutionResponse proxyInstitutionResponse = new ProxyInstitutionResponse();
        proxyInstitutionResponse.setAddress("42 Main St");
        proxyInstitutionResponse.setAoo("Aoo");
        proxyInstitutionResponse.setCategory("Category");
        proxyInstitutionResponse.setDescription("The characteristics of someone or something");
        proxyInstitutionResponse.setDigitalAddress("42 Main St");
        proxyInstitutionResponse.setId("42");
        proxyInstitutionResponse.setO("foo");
        proxyInstitutionResponse.setOrigin("Origin");
        proxyInstitutionResponse.setOriginId("42");
        proxyInstitutionResponse.setOu("Ou");
        proxyInstitutionResponse.setTaxCode("Tax Code");
        proxyInstitutionResponse.setZipCode("21654");
        when(partyRegistryProxyRestClient.getInstitutionById(any())).thenReturn(proxyInstitutionResponse);
        //when
        InstitutionProxyInfo actualInstitutionById = partyRegistryProxyConnectorImpl.getInstitutionProxyById("42");
        //then
        reflectionEqualsByName(proxyInstitutionResponse, actualInstitutionById);
        verify(partyRegistryProxyRestClient).getInstitutionById("42");
    }


    @Test
    void testGetExtByCode() {
        GeographicTaxonomiesResponse geographicTaxonomiesResponse = new GeographicTaxonomiesResponse();
        geographicTaxonomiesResponse.setGeotaxId("Code");
        geographicTaxonomiesResponse.setCountry("GB");
        geographicTaxonomiesResponse.setCountryAbbreviation("GB");
        geographicTaxonomiesResponse.setDescription("The characteristics of someone or something");
        geographicTaxonomiesResponse.setEnable(true);
        geographicTaxonomiesResponse.setIstatCode("");
        geographicTaxonomiesResponse.setProvinceId("Province");
        geographicTaxonomiesResponse.setProvinceAbbreviation("Province Abbreviation");
        geographicTaxonomiesResponse.setRegionId("us-east-2");
        when(partyRegistryProxyRestClient.getExtByCode(any())).thenReturn(geographicTaxonomiesResponse);
        GeographicTaxonomies actualExtByCode = partyRegistryProxyConnectorImpl.getExtById("Code");
        assertEquals("Code", actualExtByCode.getGeotaxId());
        assertTrue(actualExtByCode.isEnable());
        assertEquals("The characteristics of someone or something", actualExtByCode.getDescription());
        verify(partyRegistryProxyRestClient).getExtByCode(any());
    }

    @Test
    void testGetExtByCode2() {
        GeographicTaxonomiesResponse geographicTaxonomiesResponse = new GeographicTaxonomiesResponse();
        geographicTaxonomiesResponse.setGeotaxId("Code");
        geographicTaxonomiesResponse.setCountry("GB");
        geographicTaxonomiesResponse.setCountryAbbreviation("GB");
        geographicTaxonomiesResponse.setDescription("The characteristics of someone or something");
        geographicTaxonomiesResponse.setEnable(false);
        geographicTaxonomiesResponse.setIstatCode("");
        geographicTaxonomiesResponse.setProvinceId("Province");
        geographicTaxonomiesResponse.setProvinceAbbreviation("Province Abbreviation");
        geographicTaxonomiesResponse.setRegionId("us-east-2");
        when(partyRegistryProxyRestClient.getExtByCode(any())).thenReturn(geographicTaxonomiesResponse);
        GeographicTaxonomies actualExtByCode = partyRegistryProxyConnectorImpl.getExtById("Code");
        assertEquals("Code", actualExtByCode.getGeotaxId());
        assertFalse(actualExtByCode.isEnable());
        assertEquals("The characteristics of someone or something", actualExtByCode.getDescription());
        verify(partyRegistryProxyRestClient).getExtByCode(any());
    }

    @Test
    void shouldGetAoo() {
        when(partyRegistryProxyRestClient.getAooById(anyString()))
                .thenReturn(aooResponse);

        HomogeneousOrganizationalArea aoo = partyRegistryProxyConnectorImpl.getAooById("example");
        assertEquals(aoo.getCodAoo(), aooResponse.getCodAoo());
        assertEquals(aoo.getId(), aooResponse.getId());
        assertEquals(aoo.getOrigin(), aooResponse.getOrigin());
    }

    @Test
    void shouldGetUo() {
        when(partyRegistryProxyRestClient.getUoById(anyString()))
                .thenReturn(uoResponse);

        OrganizationUnit uo = partyRegistryProxyConnectorImpl.getUoById("example");
        assertEquals(uo.getUniUoCode(), uoResponse.getUniUoCode());
        assertEquals(uo.getId(), uoResponse.getId());
        assertEquals(uo.getOrigin(), uoResponse.getOrigin());
    }

}

