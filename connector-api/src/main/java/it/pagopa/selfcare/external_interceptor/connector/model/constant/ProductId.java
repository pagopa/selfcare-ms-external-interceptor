package it.pagopa.selfcare.external_interceptor.connector.model.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductId {

    PROD_INTEROP("prod-interop"),
    PROD_PN("prod-pn"),
    PROD_FD("prod-fd"),
    PROD_FD_GARANTITO("prod-fd-garantito"),
    PROD_IO("prod-io"),
    PROD_INTEROP_COLL("prod-interop-coll"),
    PROD_IO_SIGN("prod-io-sign");

    private final String value;

    ProductId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
