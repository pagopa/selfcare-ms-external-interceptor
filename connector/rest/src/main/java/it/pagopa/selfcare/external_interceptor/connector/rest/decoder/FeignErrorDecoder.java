package it.pagopa.selfcare.external_interceptor.connector.rest.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import it.pagopa.selfcare.external_interceptor.connector.exceptions.ResourceNotFoundException;

public class FeignErrorDecoder extends ErrorDecoder.Default {
    @Override
    public Exception decode(String methodKey, Response response) {
       if (response.status() == 404) {
            throw new ResourceNotFoundException();
        } else {
            return super.decode(methodKey, response);
        }
    }

}
