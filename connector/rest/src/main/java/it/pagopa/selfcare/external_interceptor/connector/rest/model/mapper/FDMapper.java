package it.pagopa.selfcare.external_interceptor.connector.rest.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.Organization;
import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationLightBeanResponse;
import it.pagopa.selfcare.external_interceptor.connector.rest.model.prod_fd.OrganizationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FDMapper {

    OrganizationLightBean toOrganizationLightBean(OrganizationLightBeanResponse model);

    @Mapping(target = "fiscalCode", source = "codiceFiscale")
    @Mapping(target = "vatNumber", source = "partitaIva")
    @Mapping(target = "guarantor", source = "garante")
    @Mapping(target = "assured", source = "garantito")
    @Mapping(target = "contractor", source = "contraente")
    Organization toOrganization(OrganizationResponse model);
}
