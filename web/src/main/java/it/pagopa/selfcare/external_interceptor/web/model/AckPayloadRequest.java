package it.pagopa.selfcare.external_interceptor.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AckPayloadRequest {

    @ApiModelProperty(value = "${swagger.external-interceptor.payload.model.message}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String message;
}
