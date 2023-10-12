package it.pagopa.selfcare.external_interceptor.connector.rest;

import it.pagopa.selfcare.external_interceptor.connector.api.MsCoreConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.rest.client.MsCoreRestClient;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper.MsCoreMapper;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core.TokensResponse;
import lombok.extern.slf4j.Slf4j;
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
        TokensResponse tokensResponse = restClient.retrieveTokensByProduct(productId, page, size);
        List<Token> tokens = tokensResponse.getItems().stream()
                .map(msCoreMapper::toToken)
                .collect(Collectors.toList());
        log.debug("retrieveTokensByProductId result = {}", tokens);
        log.trace("retrieveTokensByProductId end");
        return tokens;
    }
}
