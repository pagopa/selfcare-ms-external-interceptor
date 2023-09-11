package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.Data;

@Data
public class PspData {

    private String businessRegisterNumber;
    private String legalRegisterName;
    private String legalRegisterNumber;
    private String abiCode;
    private Boolean vatNumberGroup;
    private DpoData dpoData;//fixme: needs to be removed

}
