package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.auth.FDToken;

public interface FDApiConnector {

    FDToken getFdToken();
}
