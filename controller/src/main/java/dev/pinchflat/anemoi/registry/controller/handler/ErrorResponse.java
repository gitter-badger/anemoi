package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.List;

import dev.pinchflat.anemoi.registry.error.RegistryError;

record ErrorResponse(List<Error> errors) {
	
	public ErrorResponse(RegistryError error) {
		this(List.of(new Error(error.type().toString(),error.type().getMessage(),error.detail())));
	}

	record Error(String code, String message, Object detail){	
	}
	
}
