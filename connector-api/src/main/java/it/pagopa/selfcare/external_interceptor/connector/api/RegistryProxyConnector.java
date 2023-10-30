package it.pagopa.selfcare.external_interceptor.connector.api;

import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;

public interface RegistryProxyConnector {
    HomogeneousOrganizationalArea getAooById(String aooCode);
    OrganizationUnit getUoById(String uoCode);
    GeographicTaxonomies getExtById(String code);

    InstitutionProxyInfo getInstitutionProxyById(String externalId);
}
