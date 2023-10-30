package it.pagopa.selfcare.external_interceptor.connector.rest.model.ms_core;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.InstitutionUpdate;
import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private String id;
    private String checksum;
    private List<LegalsResponse> legals = new ArrayList<>();

    private RelationshipState status;
    private String institutionId;
    private String productId;
    private String expiringDate;
    private String contractVersion;
    private String contractTemplate;
    private String contractSigned;
    private String contentType;
    private List<TokenUserResponse> users;
    private InstitutionUpdate institutionUpdate;
    private String createdAt;
    private String updatedAt;
    private String closedAt;

    public TokenResponse(String id) {
        this.id = id;
    }
}
