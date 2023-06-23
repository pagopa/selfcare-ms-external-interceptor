package it.pagopa.selfcare.external_interceptor.connector.rest.client;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import it.pagopa.selfcare.commons.connector.rest.BaseFeignRestClientTest;
import it.pagopa.selfcare.commons.connector.rest.RestTestUtils;
import it.pagopa.selfcare.external_interceptor.connector.rest.config.InternalApiRestClientTestConfig;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.InstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UserResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestPropertySource(
        locations = "classpath:config/internal-api-rest-client.properties",
        properties = {
                "logging.level.it.pagopa.selfcare.external-interceptor.connector.rest=DEBUG",
                "spring.application.name=selc-external-interceptor-connector-rest",
                "feign.okhttp.enabled=true"
        })
@ContextConfiguration(
        initializers = InternalApiRestClientTest.RandomPortInitializer.class,
        classes = {InternalApiRestClientTestConfig.class, HttpClientConfiguration.class})
class InternalApiRestClientTest extends BaseFeignRestClientTest {
    @Order(1)
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(RestTestUtils.getWireMockConfiguration("stubs/internal-api"))
            .build();

    @Autowired
    private InternalApiRestClient restClient;


    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    String.format("INTERNAL_API_SERVICE_URL=%s",
                            wm.getRuntimeInfo().getHttpBaseUrl())
            );
        }
    }

    @Test
    void getInstitutionById_fullyValued() {
        // given
        String institutionId = "institutionId1";
        // when
        InstitutionResponse response = restClient.getInstitutionById(institutionId);
        // then
        assertNotNull(response);
    }

    @Test
    void getInstitutionById_fullyNull() {
        // given
        String institutionId = "institutionId2";
        // when
        InstitutionResponse response = restClient.getInstitutionById(institutionId);
        // then
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getDescription());
        assertNull(response.getGeographicTaxonomies());
        assertNull(response.getPaymentServiceProvider());
        assertNull(response.getDataProtectionOfficer());
    }

    @Test
    void getInstitutionProductUsers_fullyValued() {
        // given
        String institutionId = "institutionId1";
        String productId = "productId1";
        // when
        List<UserResponse> userResponse = restClient.getInstitutionProductUsers(institutionId, productId);
        // then
        assertFalse(userResponse.isEmpty());
    }

    @Test
    void getInstitutionProductUsers_emptyResult() {
        // given
        String institutionId = "institutionId2";
        String productId = "productId2";
        // when
        List<UserResponse> userResponse = restClient.getInstitutionProductUsers(institutionId, productId);
        // then
        assertTrue(userResponse.isEmpty());
    }
}