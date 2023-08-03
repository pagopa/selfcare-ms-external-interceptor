package it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy;


public enum Origin {
    MOCK("MOCK"),
    IPA("IPA"),
    SELC("SELC"),
    UNKNOWN("UNKNOWN"),
    ADE("ADE"),
    INFOCAMERE("INFOCAMERE");

    private final String value;

    Origin(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }



}