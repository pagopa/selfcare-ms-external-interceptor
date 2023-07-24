package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.api.InternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.user.User;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.InternalApiRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UserResponse;
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
    private final InternalApiRestClient restClient;

    @Autowired
    public InternalApiConnectorImpl(InstitutionResponseMapper institutionResponseMapper, UserMapper userMapper, InternalApiRestClient restClient) {
        this.institutionResponseMapper = institutionResponseMapper;
        this.userMapper = userMapper;
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



}
