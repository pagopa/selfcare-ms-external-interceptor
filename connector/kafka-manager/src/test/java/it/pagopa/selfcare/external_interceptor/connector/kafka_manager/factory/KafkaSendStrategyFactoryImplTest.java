package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class KafkaSendStrategyFactoryImplTest {

    @Mock
    private SendFdNotification fdNotificationService;

    @InjectMocks
    private KafkaSendStrategyFactoryImpl mockStrategy;

    @Test
    void createFdNotificator(){
        //given
        final String productId = "prod-fd";
        //when
        KafkaSendService service = mockStrategy.create(productId);
        //then
        assertTrue(service instanceof SendFdNotification);
    }
    
    @Test
    void createNotificatorNull(){
        //given
        final String productId = "prod-io";
        //when
        KafkaSendService service = mockStrategy.create(productId);
        //then
        assertNull(service);
    }
}