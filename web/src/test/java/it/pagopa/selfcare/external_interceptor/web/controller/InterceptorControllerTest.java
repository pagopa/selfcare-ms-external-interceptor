package it.pagopa.selfcare.external_interceptor.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.AckStatus;
import it.pagopa.selfcare.external_interceptor.core.InterceptorService;
import it.pagopa.selfcare.external_interceptor.web.model.AckPayloadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {InterceptorController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {InterceptorController.class})
class InterceptorControllerTest {
    private static final String BASE_URL = "/interceptor";
    @Autowired
    protected MockMvc mvc;
    @MockBean
    private InterceptorService interceptorService;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void ackNowledgment_ack() throws Exception {
        final String messageId = UUID.randomUUID().toString();
        final String productId = "test-prod";
        final AckStatus status = AckStatus.ACK;
        final AckPayloadRequest content = new AckPayloadRequest();
        content.setMessage("message");
        mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/acknowledgment/{productId}/message/{messageId}/status/{status}", productId, messageId, status)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    void ackNowledgment_nack() throws Exception {
        final String messageId = UUID.randomUUID().toString();
        final String productId = "test-prod";
        final AckStatus status = AckStatus.NACK;
        final AckPayloadRequest content = new AckPayloadRequest();
        content.setMessage("message");

        mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/acknowledgment/{productId}/message/{messageId}/status/{status}", productId, messageId, status)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }
}