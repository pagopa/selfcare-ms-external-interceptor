package it.pagopa.selfcare.external_interceptor.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.external_interceptor.core.SchedulerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {SchedulerController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {SchedulerController.class})
class SchedulerControllerTest {
    private static final String BASE_URL = "/scheduler";
    @Autowired
    protected MockMvc mvc;
    @MockBean
    private SchedulerService schedulerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void startScheduler() throws Exception {
        Integer size = 1;
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL)
                        .param("size", String.valueOf(size)))
                .andExpect(status().isAccepted());

        Mockito.verify(schedulerService, Mockito.times(1)).startScheduler(Optional.of(size));
    }
}