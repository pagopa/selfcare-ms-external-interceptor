package it.pagopa.selfcare.external_interceptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.selfcare.external_interceptor.connector.dao.ExceptionDaoConnectorImpl;
import it.pagopa.selfcare.external_interceptor.connector.dao.ExceptionRepository;
import it.pagopa.selfcare.external_interceptor.connector.dao.model.ExceptionsEntity;
import it.pagopa.selfcare.external_interceptor.connector.model.dao_operations.ExceptionOperations;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.util.TimeZone;
import java.util.UUID;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExceptionDaoConnectorImplTest {

    private final ExceptionRepository repositoryMock;

    private final ExceptionDaoConnectorImpl exceptionDaoConnector;


    private ObjectMapper mapper;

    ExceptionDaoConnectorImplTest() {
        this.repositoryMock = mock(ExceptionRepository.class);
        this.exceptionDaoConnector = new ExceptionDaoConnectorImpl(repositoryMock);
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setTimeZone(TimeZone.getDefault());
    }

    @AfterEach
    void clear() {
        repositoryMock.deleteAll();
        Mockito.reset(repositoryMock);
    }

    @Test
    void insert() throws JsonProcessingException {
        // given
        Notification notification = mockInstance(new Notification());
        String id = UUID.randomUUID().toString();
        notification.setId(id);
        InvalidFormatException formatException = mock(InvalidFormatException.class);
        String payload = mapper.writeValueAsString(notification);
        when(repositoryMock.insert(any(ExceptionsEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    ExceptionsEntity argument = invocationOnMock.getArgument(0);
                    return argument;
                });

        // when
        ExceptionOperations saved = exceptionDaoConnector.insert(payload, formatException);
        // then
        verify(repositoryMock, times(1))
                .insert(any(ExceptionsEntity.class));
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_duplicateKey() throws JsonProcessingException {
        // given
        Notification notification = mockInstance(new Notification());
        String id = UUID.randomUUID().toString();
        notification.setId(id);
        InvalidFormatException formatException = mock(InvalidFormatException.class);
        String payload = mapper.writeValueAsString(notification);
        doThrow(DuplicateKeyException.class).when(repositoryMock).insert(any(ExceptionsEntity.class));
        // when
        Executable executable = () -> exceptionDaoConnector.insert(payload, formatException);
        // then
        assertDoesNotThrow(executable);
        verify(repositoryMock, times(1))
                .insert(any(ExceptionsEntity.class));
        verify(repositoryMock, times(1))
                .save(any(ExceptionsEntity.class));
        verifyNoMoreInteractions(repositoryMock);
    }


}