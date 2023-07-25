package it.pagopa.selfcare.external_interceptor.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.AckStatus;
import it.pagopa.selfcare.external_interceptor.core.InterceptorService;
import it.pagopa.selfcare.external_interceptor.web.model.AckPayloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/interceptor")
@Api(tags = "interceptor")
@Slf4j
public class InterceptorController {

    private final InterceptorService interceptorService;


    @Autowired
    public InterceptorController(InterceptorService interceptorService) {
        this.interceptorService = interceptorService;
    }
    @ApiOperation(value = "", notes = "${swagger.external-interceptor.acknowledgment.api.messageAcknowledgment}")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/acknowledgment/{productId}/message/{messageId}/status/{status}")
    public void messageAcknowledgment(@ApiParam("${swagger.external-interceptor.product.model.id}") @PathVariable("productId")String productId,
                                      @ApiParam("${swagger.external-interceptor.message.model.id}")@PathVariable("messageId")String messageId,
                                      @ApiParam("${swagger.external-interceptor.message.model.status}")@PathVariable("status") AckStatus status,
                                      @RequestBody @Valid AckPayloadRequest payload){
        log.trace("messageAcknowledgment start");
        log.debug("productId = {}, messageId = {}, status = {}, payload = {}", productId, messageId, status, payload);
        if(AckStatus.ACK.equals(status))
            log.info("[SUCCESSFUL Acknowledgment] - Consumer acknowledged message: {} consumption, for product = {}", messageId, productId);
        else {
            log.error("[ACKNOWLEDGMENT ERROR] - record with {} id gave {}, it wasn't processed correctly by {}, reason = {}", messageId, status, productId, payload.getMessage());
        }
        log.trace("messageAcknowledgment end");
    }

}
