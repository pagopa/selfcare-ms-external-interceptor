package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataProtectionOfficer {

    private String address;
    private String email;
    private String pec;

}
