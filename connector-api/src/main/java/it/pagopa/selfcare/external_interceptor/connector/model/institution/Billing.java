package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

@Data
public class Billing {
    private String vatNumber;
    private String recipientCode;
    private boolean publicService;

}
