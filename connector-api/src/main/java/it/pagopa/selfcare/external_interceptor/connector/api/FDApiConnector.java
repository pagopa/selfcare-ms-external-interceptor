package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.auth.OauthToken;

public interface FDApiConnector {

    OauthToken getFdToken();
}