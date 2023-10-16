package it.pagopa.selfcare.external_interceptor.connector.model.user;

import it.pagopa.selfcare.commons.base.security.PartyRole;
import lombok.Data;

import java.util.List;

@Data
public class OnboardedUserProduct {
    private String productId;
    private PartyRole role;
    private List<String> roles;
    private String createdAt;
}
