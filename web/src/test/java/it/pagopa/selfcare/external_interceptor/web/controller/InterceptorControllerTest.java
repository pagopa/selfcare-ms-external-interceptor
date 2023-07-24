package it.pagopa.selfcare.external_interceptor.web.controller;

import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.AckStatus;
import it.pagopa.selfcare.external_interceptor.core.InterceptorService;
import it.pagopa.selfcare.external_interceptor.web.model.AckPayloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {InterceptorController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {InterceptorController.class})
@ExtendWith(MockitoExtension.class)
class InterceptorControllerTest {
    private static final String BASE_URL = "/interceptor";
    @InjectMocks
    private InterceptorController interceptorController;
    @Autowired
    protected MockMvc mvc;
    @Mock
    private InterceptorService interceptorService;

    @Test
    void ackNowledgment_ack() throws Exception {
        final String messageId = UUID.randomUUID().toString();
        final String productId = "test-prod";
        final AckStatus status = AckStatus.ACK;
        final AckPayloadRequest content = new AckPayloadRequest();

        mvc.perform(MockMvcRequestBuilders
                .post("/acknowledgment/{productId}/message/{messageId}/status/{status}", productId, messageId, status)
                .content(String.valueOf(content))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    void ackNowledgment_nack() throws Exception {
        final String messageId = UUID.randomUUID().toString();
        final String productId = "test-prod";
        final AckStatus status = AckStatus.NACK;
        final AckPayloadRequest content = new AckPayloadRequest();

        mvc.perform(MockMvcRequestBuilders
                .post("/acknowledgment/{productId}/message/{messageId}/status/{status}", productId, messageId, status)
                .content(String.valueOf(content))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}