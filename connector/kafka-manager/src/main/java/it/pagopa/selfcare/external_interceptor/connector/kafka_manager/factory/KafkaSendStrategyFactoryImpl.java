package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSendStrategyFactoryImpl implements KafkaSendStrategyFactory{

    private final SendFdNotification fdNotification;

    @Autowired
    public KafkaSendStrategyFactoryImpl(SendFdNotification fdNotification) {
        this.fdNotification = fdNotification;
    }

    @Override
    public KafkaSendService create(String productId) {
        final KafkaSendService service;
        switch (productId){
            case "prod-fd":
                service = this.fdNotification;
                break;
            default:
                return null;
        }

        return service;
    }
}
