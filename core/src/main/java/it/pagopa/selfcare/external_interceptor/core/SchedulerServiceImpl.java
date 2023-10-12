package it.pagopa.selfcare.external_interceptor.core;

import it.pagopa.selfcare.external_interceptor.connector.api.KafkaSapSendService;
import it.pagopa.selfcare.external_interceptor.connector.api.MsCoreConnector;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.ms_core.Token;
import it.pagopa.selfcare.external_interceptor.core.config.ScheduledConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SchedulerServiceImpl{
    private final MsCoreConnector msCoreConnector;
    public static final int TOKEN_PAGE_SIZE = 100;
    private final KafkaSapSendService kafkaSapSendService;
    private final List<String> productsToRetrieve;
    private final ScheduledConfig configProperties;
    public SchedulerServiceImpl(MsCoreConnector msCoreConnector,
                                KafkaSapSendService kafkaSapSendService,
                                @Value("${external-interceptor.scheduler.products-to-resend}") List<String> productsToRetrieve,
                                ScheduledConfig configProperties) {
        this.msCoreConnector = msCoreConnector;
        this.kafkaSapSendService = kafkaSapSendService;
        this.productsToRetrieve = productsToRetrieve;
        this.configProperties = configProperties;
    }

    @Scheduled(fixedDelayString = "scheduler.fixed-delay.delay")
    void regenerateQueueNotifications(){
        log.trace("regenerateQueueNotifications start");
        if (configProperties.getSendOldEvent()){
            for (String productId: productsToRetrieve) {
                int page = 0;
                boolean nextPage = true;
                do {
                    List<Token> tokens = msCoreConnector.retrieveTokensByProductId(productId, page, TOKEN_PAGE_SIZE);
                    log.debug("[KAFKA] TOKEN NUMBER {} PAGE {}", tokens.size(), page);
                    Notification notification = null;

                    page += 1;
                    if (tokens.size() < TOKEN_PAGE_SIZE) {
                        nextPage = false;
                        log.debug("[KAFKA] TOKEN TOTAL NUMBER {}", page * TOKEN_PAGE_SIZE + tokens.size());
                    }

                } while (nextPage);
            }
            configProperties.setScheduler(false);
        }
    }

    private void sendSapNotifications(List<Token> tokens){
        tokens.forEach(token -> {

        });
    }
}
