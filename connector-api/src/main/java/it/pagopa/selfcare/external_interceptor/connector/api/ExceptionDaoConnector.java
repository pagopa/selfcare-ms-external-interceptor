package it.pagopa.selfcare.external_interceptor.connector.api;


import it.pagopa.selfcare.external_interceptor.connector.model.dao_operations.ExceptionOperations;

public interface ExceptionDaoConnector {

    ExceptionOperations insert(String payload, Exception exception);
}
