package it.pagopa.selfcare.external_interceptor.core;

public interface InterceptorService {
    boolean checkOrganization(String fiscalCode, String vatNumber);
}
