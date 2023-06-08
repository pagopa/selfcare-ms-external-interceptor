package it.pagopa.selfcare.external_interceptor.connector.dao;

import it.pagopa.selfcare.external_interceptor.connector.api.ExceptionDaoConnector;
import it.pagopa.selfcare.external_interceptor.connector.dao.model.ExceptionsEntity;
import it.pagopa.selfcare.external_interceptor.connector.model.dao_operations.ExceptionOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExceptionDaoConnectorImpl implements ExceptionDaoConnector {

    private final ExceptionRepository repository;


    @Autowired
    public ExceptionDaoConnectorImpl(ExceptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public ExceptionOperations insert(String value, Exception exception) {
        log.trace("insert start");
        log.debug("insert record value = {}. exception = {}", value, exception);
        ExceptionsEntity insert;
        final ExceptionsEntity entity = new ExceptionsEntity();
        String id = value.substring(6, 43);
        entity.setId(id);
        entity.setNotification(value);
        entity.setExceptionDescription(exception.getMessage());
        entity.setException(exception.getClass().getSimpleName());
        try{
            insert = repository.insert(entity);
            log.debug("saved exception = {}", insert);
        } catch (DuplicateKeyException e){
            entity.setNew(false);
            insert = repository.save(entity);
            log.debug("updated exception = {}", insert);
        }
        return insert;
    }

}
