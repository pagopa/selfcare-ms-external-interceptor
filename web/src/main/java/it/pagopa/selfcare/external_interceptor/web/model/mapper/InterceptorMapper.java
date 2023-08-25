package it.pagopa.selfcare.external_interceptor.web.model.mapper;

import it.pagopa.selfcare.external_interceptor.connector.model.prod_fd.OrganizationLightBean;
import it.pagopa.selfcare.external_interceptor.web.model.OrganizationLightBeanResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InterceptorMapper {
    OrganizationLightBeanResource toResource(OrganizationLightBean model);
}
