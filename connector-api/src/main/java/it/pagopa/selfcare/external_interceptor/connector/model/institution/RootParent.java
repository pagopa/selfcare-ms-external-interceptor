package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootParent {
    private String id;
    private String originId;
    private String description;
}
