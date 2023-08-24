package it.pagopa.selfcare.external_interceptor.connector.api;

public interface FDApiConnector {

    String getFdToken(String grantType, String clientId, String clientSecret);
}
