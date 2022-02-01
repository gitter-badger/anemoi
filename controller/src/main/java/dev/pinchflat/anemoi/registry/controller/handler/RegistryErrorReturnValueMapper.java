package dev.pinchflat.anemoi.registry.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.RegistryErrorResponse;

@Component
public class RegistryErrorReturnValueMapper extends AbstractReturnValueHandler<RegistryErrorResponse> {

	protected RegistryErrorReturnValueMapper() {
		super(RegistryErrorResponse.class);
	}

	@Override
	public HttpStatus getStatus(RegistryErrorResponse value) {
		return HttpStatus.valueOf(value.error().type().getStatus());
	}

	@Override
	public ErrorResponse getResponseObject(String method, RegistryErrorResponse value) {
		return new ErrorResponse(value.error());
	}

}
