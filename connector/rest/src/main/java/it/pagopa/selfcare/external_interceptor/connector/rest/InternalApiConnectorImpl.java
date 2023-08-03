package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.api.InternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;
import it.pagopa.selfcare.external_interceptor.connector.model.user.User;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.InternalApiRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.*;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.EntityMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.InstitutionResponseMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternalApiConnectorImpl implements InternalApiConnector {

    protected static final String EXTERNAL_ID_IS_REQUIRED = "An institution externalId is required";
    protected static final String PRODUCT_ID_IS_REQUIRED = "A productId is required";
    protected static final String INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";
    private final InstitutionResponseMapper institutionResponseMapper;

    private final UserMapper userMapper;

    private final EntityMapper entityMapper;
    private final InternalApiRestClient restClient;

    @Autowired
    public InternalApiConnectorImpl(InstitutionResponseMapper institutionResponseMapper, UserMapper userMapper, EntityMapper entityMapper, InternalApiRestClient restClient) {
        this.institutionResponseMapper = institutionResponseMapper;
        this.userMapper = userMapper;
        this.entityMapper = entityMapper;
        this.restClient = restClient;
    }


    @Override
    public Institution getInstitutionById(String institutionId) {
        log.trace("getInstitutionById start");
        log.debug("getInstitutionById institutionId = {}", institutionId);
        Assert.hasText(institutionId, INSTITUTION_ID_IS_REQUIRED);
        InstitutionResponse response = restClient.getInstitutionById(institutionId);
        Institution result = institutionResponseMapper.toInstitution(response);
        log.debug("getInstitutionById result = {}", result);
        log.trace("getInstitutionById end");
        return result;
    }

    @Override
    public List<User> getInstitutionProductUsers(String institutionId, String productId) {
        log.trace("getInstitutionProductUsers start");
        log.debug("getInstitutionProductUsers institutionId = {}, productId = {}", institutionId, productId);
        Assert.hasText(institutionId, INSTITUTION_ID_IS_REQUIRED);
        Assert.hasText(productId, PRODUCT_ID_IS_REQUIRED);
        List<UserResponse> userResponse = restClient.getInstitutionProductUsers(institutionId, productId);
        List<User> user = userResponse.stream().map(userMapper::toUser).collect(Collectors.toList());
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getInstitutionProductUsers user = {}", user);
        log.trace("getInstitutionProductUsers end");
        return user;
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

    @Override
    public InstitutionProxyInfo getInstitutionProxyById(String institutionId) {
        log.trace("getInstitutionProxyById start");
        log.debug("getInstitutionProxyById institutionId = {}", institutionId);
        ProxyInstitutionResponse proxyInstitutionResponse = restClient.getRegistryInstitutionById(institutionId);
        InstitutionProxyInfo result = entityMapper.toInstitutionProxyInfo(proxyInstitutionResponse);
        log.debug("getInstitutionProxyById result = {}", result);
        log.trace("getInstitutionProxyById end");
        return result;
    }

}
