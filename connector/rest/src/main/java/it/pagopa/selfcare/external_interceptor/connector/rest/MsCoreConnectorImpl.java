package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.core.generated.openapi.v1.dto.InstitutionResponse;
import it.pagopa.selfcare.core.generated.openapi.v1.dto.TokenListResponse;
import it.pagopa.selfcare.core.generated.openapi.v1.dto.TokenResponse;
import it.pagopa.selfcare.external_interceptor.connector.api.MsCoreConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.MsCoreRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.MsCoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MsCoreConnectorImpl implements MsCoreConnector {
    private final MsCoreRestClient restClient;
    private final MsCoreMapper msCoreMapper;

    public MsCoreConnectorImpl(MsCoreRestClient restClient, MsCoreMapper msCoreMapper) {
        this.restClient = restClient;
        this.msCoreMapper = msCoreMapper;
    }

    @Override
    public List<Token> retrieveTokensByProductId(String productId, Integer page, Integer size) {
        log.trace("retrieveTokensByProductId start");
        log.debug("retrieveTokensByProductId productId = {}, page = {}, size = {}", productId, page, size);
        ResponseEntity<TokenListResponse> tokensResponse = restClient._findFromProductUsingGET1(productId, page, size);
        List<TokenResponse> items = tokensResponse.getBody().getItems();
        List<Token> collect = items.stream().map(msCoreMapper::toToken)
                .collect(Collectors.toList());
//        List<Token> tokens = Objects.requireNonNull(tokensResponse.getBody()).getItems().stream()
//                .map(tokenResponse -> msCoreMapper.toToken(tokenResponse))
//                .collect(Collectors.toList());
        log.debug("retrieveTokensByProductId result = {}", collect);
        log.trace("retrieveTokensByProductId end");
        return collect;
    }

    @Override
    public Institution getInstitutionById(String institutionId){
        log.trace("getInstitutionById start");
        log.debug("getInstitutionById institutionId = {}", institutionId);
        ResponseEntity<InstitutionResponse> institutionResponse = restClient._retrieveInstitutionByIdUsingGET(institutionId);
        Institution institution = msCoreMapper.toInstitution(institutionResponse.getBody());
        log.debug("getInstitutionById result = {}", institution);
        return institution;
    }
}
