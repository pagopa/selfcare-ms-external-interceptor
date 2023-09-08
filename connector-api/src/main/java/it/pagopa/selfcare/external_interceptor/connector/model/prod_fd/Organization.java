package it.pagopa.selfcare.external_interceptor.connector.model.prod_fd;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Organization {
    private String id;
    private String fiscalCode;
    private String vatNumber;
    private String legalName;
    private String status;
    private String city;
    private String province;
    private String address;
    private String streetNumber;
    private String zipCode;
    private boolean guarantor;
    private boolean assured;
    private boolean contractor;
    private String typeOfCounterparty;
    private LocalDateTime creationDate;
    private LocalDateTime activationDate;
}
