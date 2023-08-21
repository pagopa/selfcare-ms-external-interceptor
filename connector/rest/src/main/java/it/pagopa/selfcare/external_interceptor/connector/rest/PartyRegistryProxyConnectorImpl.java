package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.RegistryProxyConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.PartyRegistryProxyRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.AooResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.GeographicTaxonomiesResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UoResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PartyRegistryProxyConnectorImpl implements RegistryProxyConnector {
    private final PartyRegistryProxyRestClient restClient;
    private final EntityMapper entityMapper;

    public PartyRegistryProxyConnectorImpl(PartyRegistryProxyRestClient restClient, EntityMapper entityMapper) {
        this.restClient = restClient;
        this.entityMapper = entityMapper;
    }

    @Override
    public HomogeneousOrganizationalArea getAooById(String aooCode) {
        log.trace("getAooById start");
        log.debug("getAooById aooCode = {}", aooCode);
        AooResponse aooResponse = restClient.getAooById(aooCode);
        HomogeneousOrganizationalArea result = entityMapper.toAOO(aooResponse);
        log.debug("getAooById result = {}", result);
        log.trace("getAooById end");
        return result;
    }

    @Override
    public OrganizationUnit getUoById(String uoCode) {
        log.trace("getUoById start");
        log.debug("getUoById uoCode = {}", uoCode);
        UoResponse uoResponse = restClient.getUoById(uoCode);
        OrganizationUnit result = entityMapper.toUO(uoResponse);
        log.debug("getUoById result = {}", result);
        log.trace("getUoById end");
        return result;
    }

    @Override
    public GeographicTaxonomies getExtById(String code){
        log.trace("getExtById start");
        log.debug("getExtById code = {}", code);
        GeographicTaxonomiesResponse geographicTaxonomiesResponse = restClient.getExtByCode(code);
        GeographicTaxonomies result = entityMapper.toGeographicTaxonomies(geographicTaxonomiesResponse);
        log.debug("getExtById result = {}", result);
        log.trace("getExtById end");
        return result;
    }

}
