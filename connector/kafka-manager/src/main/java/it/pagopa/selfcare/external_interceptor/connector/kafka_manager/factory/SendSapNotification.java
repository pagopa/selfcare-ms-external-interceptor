package it.pagopa.selfcare.external_interceptor.connector.kafka_manager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.base.logging.LogUtils;
import it.pagopa.selfcare.commons.base.utils.InstitutionType;
import it.pagopa.selfcare.external_interceptor.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.external_interceptor.connector.api.KafkaSapSendService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Qualifier("sapNotificator")
public class SendSapNotification extends KafkaSend implements KafkaSapSendService {
    static final String DESCRIPTION_TO_REPLACE_REGEX = " - COMUNE";
    public static final String SC_CONTRACTS_SAP = "Sc-Contracts-Sap";
    private final Optional<Set<InstitutionType>> allowedInstitutionTypes;
    private final Optional<List<String>> allowedProducts;

    public SendSapNotification(@Autowired
                               @Qualifier("sapProducer")
                               KafkaTemplate<String, String> kafkaTemplate,
                               NotificationMapper notificationMapper,
                               ObjectMapper mapper,
                               RegistryProxyConnector registryProxyConnector,
                               ExternalApiConnector externalApiConnector,
                               @Value("${external-interceptor.sap.allowed-institution-types}") Set<InstitutionType> allowedInstitutionTypes,
                               @Value("#{'${external-interceptor.scheduler.products-to-resend}'.split(',')}") List<String> allowedProducts) {
        super(kafkaTemplate, notificationMapper, mapper, registryProxyConnector, externalApiConnector);
        this.allowedInstitutionTypes = Optional.ofNullable(allowedInstitutionTypes);
        this.allowedProducts = Optional.ofNullable(allowedProducts);
    }

    @Override
    public void sendInstitutionNotification(Notification notification, Acknowledgment acknowledgment) throws JsonProcessingException {
        log.trace("sendInstitutionNotification start");
        if (checkAllowedNotification(notification)) {
            log.debug(LogUtils.CONFIDENTIAL_MARKER, "send institution notification = {}", notification);
            NotificationToSend notificationToSend = notificationMapper.createInstitutionNotification(notification);
                setNotificationInstitutionLocationFields(notificationToSend);
                notificationToSend.setType(NotificationType.ADD_INSTITUTE);
                String institutionNotification = mapper.writeValueAsString(notificationToSend);
                String logSuccess = String.format("sent notification for token : %s, to SAP", notification.getOnboardingTokenId());
                String logFailure = String.format("error during notification sending for token %s: {}, on SAP ", notification.getOnboardingTokenId());
                sendNotification(institutionNotification, SC_CONTRACTS_SAP, logSuccess, logFailure, Optional.ofNullable(acknowledgment));
                log.trace("sendInstitutionNotification end");
        }
    }

    private boolean checkAllowedNotification(Notification notification){
        return allowedProducts.isPresent()
                && allowedProducts.get().contains(notification.getProduct())
                && allowedInstitutionTypes.isPresent()
                && notification.getInstitution().getOrigin().equals("IPA")
                && allowedInstitutionTypes.get().contains(notification.getInstitution().getInstitutionType());
    }

    private void setNotificationInstitutionLocationFields(NotificationToSend notificationToSend) {
        try {
            GeographicTaxonomies geographicTaxonomies = null;
            if (notificationToSend.getInstitution().getSubUnitType() != null) {
                switch (notificationToSend.getInstitution().getSubUnitType()) {
                    case "UO":
                        OrganizationUnit organizationUnit = registryProxyConnector.getUoById(notificationToSend.getInstitution().getSubUnitCode());
                        notificationToSend.getInstitution().setIstatCode(organizationUnit.getMunicipalIstatCode());
                        geographicTaxonomies = registryProxyConnector.getExtById(organizationUnit.getMunicipalIstatCode());
                        break;
                    case "AOO":
                        HomogeneousOrganizationalArea homogeneousOrganizationalArea = registryProxyConnector.getAooById(notificationToSend.getInstitution().getSubUnitCode());
                        notificationToSend.getInstitution().setIstatCode(homogeneousOrganizationalArea.getMunicipalIstatCode());
                        geographicTaxonomies = registryProxyConnector.getExtById(homogeneousOrganizationalArea.getMunicipalIstatCode());
                        break;
                    default:
                        break;
                }
            }
            if (geographicTaxonomies != null) {
                notificationToSend.getInstitution().setCounty(geographicTaxonomies.getProvinceAbbreviation());
                notificationToSend.getInstitution().setCountry(geographicTaxonomies.getCountryAbbreviation());
                notificationToSend.getInstitution().setCity(geographicTaxonomies.getDescription().replace(DESCRIPTION_TO_REPLACE_REGEX, ""));
            }
        } catch (ResourceNotFoundException e) {
            log.warn("Error while searching institution {} on IPA, {} ", notificationToSend.getInstitution().getDescription(), e.getMessage());
            notificationToSend.getInstitution().setIstatCode(null);
        }
    }

    @Override
    public void sendUserNotification(UserNotification userNotification, Acknowledgment acknowledgment) throws JsonProcessingException {

    }

    @Override
    public void sendOldEvents(Notification notification) throws JsonProcessingException {
        sendInstitutionNotification(notification, null);
    }
}
