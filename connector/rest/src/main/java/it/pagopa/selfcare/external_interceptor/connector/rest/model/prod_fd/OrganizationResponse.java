package it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganizationResponse {
    private String id;
    private String codiceFiscale;
    private String partitaIva;
    private String legalName;
    private String status;
    private String city;
    private String province;
    private String address;
    private String streetNumber;
    private String zipCode;
    private boolean garante;
    private boolean garantito;
    private boolean contraente;
    private String typeOfCounterparty;
    private LocalDateTime creationDate;
    private LocalDateTime activationDate;
}
