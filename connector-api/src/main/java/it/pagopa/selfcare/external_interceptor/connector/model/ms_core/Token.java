package it.pagopa.selfcare.external_interceptor.connector.model.ms_core;

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
public class Token {
    private String id;
    private String checksum;
    private List<Legals> legals = new ArrayList<>();

    private RelationshipState status;
    private String institutionId;
    private String productId;
    private String expiringDate;
    private String contractVersion;
    private String contractTemplate;
    private String contractSigned;
    private String contentType;
    private List<TokenUser> users;
    private InstitutionUpdate institutionUpdate;
    private String createdAt;
    private String updatedAt;
    private String activatedAt;
    private String deletedAt;
    private String closedAt;
}
