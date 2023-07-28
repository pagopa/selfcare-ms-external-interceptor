package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

public interface KafkaSendStrategyFactory {

    KafkaSendService create(String productId);
}
