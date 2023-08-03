package it.pagopa.selfcare.external_interceptor.connector.api;


import it.pagopa.selfcare.external_interceptor.connector.model.institution.Institution;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;
import it.pagopa.selfcare.external_interceptor.connector.model.user.User;

import java.util.List;

public interface InternalApiConnector {

    Institution getInstitutionById(String institutionId);

    List<User> getInstitutionProductUsers(String institutionId, String productId);

    HomogeneousOrganizationalArea getAooById(String aooCode);

    OrganizationUnit getUoById(String uoCode);
    GeographicTaxonomies getExtById(String code);
    InstitutionProxyInfo getInstitutionProxyById(String institutionId);

}
