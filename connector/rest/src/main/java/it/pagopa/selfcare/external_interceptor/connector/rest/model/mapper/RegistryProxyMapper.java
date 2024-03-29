package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.GeographicTaxonomies;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.HomogeneousOrganizationalArea;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.InstitutionProxyInfo;
import it.pagopa.selfcare.external_interceptor.connector.model.registry_proxy.OrganizationUnit;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.AooResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.GeographicTaxonomiesResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.ProxyInstitutionResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.UoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistryProxyMapper {

    GeographicTaxonomies toGeographicTaxonomies(GeographicTaxonomiesResponse entity);

    InstitutionProxyInfo toInstitutionProxyInfo(ProxyInstitutionResponse entity);

    HomogeneousOrganizationalArea toAOO(AooResponse entity);

    OrganizationUnit toUO(UoResponse entity);

}
