package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.api.RegistryProxyConnector;
import it.pagopa.selfcare.external_interceptor.connector.exceptions.ResourceNotFoundException;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.Notification;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationToSend;
import it.pagopa.selfcare.external_interceptor.connector.model.institution.NotificationType;
import it.pagopa.selfcare.external_interceptor.connector.model.mapper.NotificationMapper;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;
import it.pagopa.selfcare.external_interceptor.connector.model.user.UserNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Qualifier("sapNotificator")
public class SendSapNotification extends KafkaSend {
    static final String DESCRIPTION_TO_REPLACE_REGEX = " - COMUNE";
    public SendSapNotification(@Autowired
                               @Qualifier("sapProducer")
                               KafkaTemplate<String, String> kafkaTemplate,
                               NotificationMapper notificationMapper,
                               ObjectMapper mapper,
                               RegistryProxyConnector registryProxyConnector,
                               ExternalApiConnector externalApiConnector) {
        super(kafkaTemplate, notificationMapper, mapper, registryProxyConnector, externalApiConnector);
    }

    @Override
    public void sendInstitutionNotification(Notification notification, Acknowledgment acknowledgment) throws JsonProcessingException {
        log.trace("sendInstitutionNotification start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "send institution notification = {}", notification);
        NotificationToSend notificationToSend = notificationMapper.createInstitutionNotification(notification);
        try {
            GeographicTaxonomies geographicTaxonomies = null;
            if(notification.getInstitution().getSubUnitType()!= null) {
                switch (notification.getInstitution().getSubUnitType()) {
                    case "UO":
                        OrganizationUnit organizationUnit = registryProxyConnector.getUoById(notification.getInstitution().getSubUnitCode());
                        notificationToSend.getInstitution().setIstatCode(organizationUnit.getMunicipalIstatCode());
                        geographicTaxonomies = registryProxyConnector.getExtById(organizationUnit.getMunicipalIstatCode());
                        break;
                    case "AOO":
                        HomogeneousOrganizationalArea homogeneousOrganizationalArea = registryProxyConnector.getAooById(notification.getInstitution().getSubUnitCode());
                        notificationToSend.getInstitution().setIstatCode(homogeneousOrganizationalArea.getMunicipalIstatCode());
                        geographicTaxonomies = registryProxyConnector.getExtById(homogeneousOrganizationalArea.getMunicipalIstatCode());
                        break;
                    default:
                        break;
                }
            }
            if(geographicTaxonomies != null) {
                notificationToSend.getInstitution().setCounty(geographicTaxonomies.getProvinceAbbreviation());
                notificationToSend.getInstitution().setCountry(geographicTaxonomies.getCountryAbbreviation());
                notificationToSend.getInstitution().setCity(geographicTaxonomies.getDescription().replace(DESCRIPTION_TO_REPLACE_REGEX, ""));
            }
        } catch (ResourceNotFoundException e) {
            log.warn("Error while searching institution {} on IPA, {} ", notificationToSend.getInstitution().getDescription(), e.getMessage());
            notificationToSend.getInstitution().setIstatCode(null);
        }
        notificationToSend.setType(NotificationType.ADD_INSTITUTE);
        String institutionNotification = mapper.writeValueAsString(notificationToSend);
        String logSuccess = String.format("sent notification for token : %s, to SAP", notification.getOnboardingTokenId());
        String logFailure = String.format("error during notification sending for token %s: {}, on SAP ", notification.getOnboardingTokenId());
        sendNotification(institutionNotification, "Sc-Contracts-Sap", logSuccess, logFailure, Optional.of(acknowledgment));
        log.trace("sendInstitutionNotification end");

    }

    @Override
    public void sendUserNotification(UserNotification userNotification, Acknowledgment acknowledgment) throws JsonProcessingException {

    }

}
