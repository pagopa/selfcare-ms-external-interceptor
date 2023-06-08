package it.pagopa.selfcare.external_interceptor.connector.dao;

import it.pagopa.selfcare.external_interceptor.connector.dao.model.ExceptionsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExceptionRepository extends MongoRepository<ExceptionsEntity, String> {
}
