package it.pagopa.selfcare.external_interceptor.connector.rest.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OauthToken {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonCreator
    public static OauthToken create(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") String expiresIn) {
        return new OauthToken(accessToken, expiresIn);
    }
}
