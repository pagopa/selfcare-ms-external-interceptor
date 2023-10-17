package it.pagopa.selfcare.external_interceptor.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.selfcare.external_interceptor.connector.api.KafkaSapSendService;
import it.pagopa.selfcare.external_interceptor.connector.api.MsCoreConnector;
import it.pagopa.selfcare.external_interceptor.connector.api.RegistryProxyConnector;
import it.pagopa.selfcare.external_interceptor.connector.exceptions.ResourceNotFoundException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.RootParent;
import it.pagopa.selfcare.external_interceptor.connector.model.interceptor.QueueEvent;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;
import it.pagopa.selfcare.external_interceptor.core.config.ScheduledConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService{
    private final MsCoreConnector msCoreConnector;
    public static final int TOKEN_PAGE_SIZE = 100;
    private Optional<Integer> token_page_size_api = Optional.empty();
    private final KafkaSapSendService kafkaSapSendService;
    private final List<String> productsToRetrieve;
    private final ScheduledConfig configProperties;
    private final NotificationMapper notificationMapper;
    private final RegistryProxyConnector partyRegistryProxyConnector;
    static final String DESCRIPTION_TO_REPLACE_REGEX = " - COMUNE";
    public SchedulerServiceImpl(MsCoreConnector msCoreConnector,
                                KafkaSapSendService kafkaSapSendService,
                                @Value("#{'${external-interceptor.scheduler.products-to-resend}'.split(',')}") List<String> productsToRetrieve,
                                ScheduledConfig configProperties,
                                NotificationMapper notificationMapper,
                                RegistryProxyConnector partyRegistryProxyConnector) {
        this.msCoreConnector = msCoreConnector;
        this.kafkaSapSendService = kafkaSapSendService;
        this.productsToRetrieve = productsToRetrieve;
        this.configProperties = configProperties;
        this.notificationMapper = notificationMapper;
        this.partyRegistryProxyConnector = partyRegistryProxyConnector;
    }

    @Scheduled(fixedDelayString = "${scheduler.fixed-delay.delay}")
    void regenerateQueueNotifications() {
        log.trace("regenerateQueueNotifications start");
        if (configProperties.getSendOldEvent()) {
            for (String productId : productsToRetrieve) {
                int page = 0;
                boolean nextPage = true;
                do {
                    List<Token> tokens = msCoreConnector.retrieveTokensByProductId(productId, page, token_page_size_api.orElse(TOKEN_PAGE_SIZE));
                    log.debug("[KAFKA] TOKEN NUMBER {} PAGE {}", tokens.size(), page);

                    sendSapNotifications(tokens);
                    page += 1;
                    if (tokens.size() < TOKEN_PAGE_SIZE) {
                        nextPage = false;
                        log.debug("[KAFKA] TOKEN TOTAL NUMBER {}", page * TOKEN_PAGE_SIZE + tokens.size());
                    }

                } while (nextPage);
            }
            this.token_page_size_api = Optional.empty();
            configProperties.setScheduler(false);
        }
        log.info("Next scheduled check at {}", OffsetDateTime.now().plusSeconds(configProperties.getFixedDelay() / 1000));
        log.trace("regenerateQueueNotifications end");

    }

    private void sendSapNotifications(List<Token> tokens) {
        tokens.forEach(token -> {
            if(token.getStatus().equals(RelationshipState.ACTIVE)) {
                Institution institution = msCoreConnector.getInstitutionById(token.getInstitutionId());
                RootParent rootParent = new RootParent();
                rootParent.setDescription(institution.getParentDescription());
                Notification toNotify = token.getStatus().equals(RelationshipState.ACTIVE)
                        ? notificationMapper.toNotificationToSend(institution, token, QueueEvent.ADD)
                        : notificationMapper.toNotificationToSend(institution, token, QueueEvent.UPDATE);
                try {
                    InstitutionProxyInfo institutionProxyInfo = partyRegistryProxyConnector.getInstitutionProxyById(institution.getExternalId());
                    institution.setIstatCode(institutionProxyInfo.getIstatCode());
                    GeographicTaxonomies geographicTaxonomies = partyRegistryProxyConnector.getExtById(institution.getIstatCode());
                    institution.setCounty(geographicTaxonomies.getProvinceAbbreviation());
                    institution.setCountry(geographicTaxonomies.getCountryAbbreviation());
                    institution.setCity(geographicTaxonomies.getDescription().replace(DESCRIPTION_TO_REPLACE_REGEX, ""));
                } catch (ResourceNotFoundException e) {
                    log.warn("Error while searching institution {} on IPA, {} ", institution.getExternalId(), e.getMessage());
                    institution.setIstatCode(null);
                }
                toNotify.setInstitution(institution);
                try {
                    kafkaSapSendService.sendOldEvents(toNotify);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void startScheduler(Optional<Integer> size) {
        this.token_page_size_api = size;
        configProperties.setScheduler(true);
    }
}
