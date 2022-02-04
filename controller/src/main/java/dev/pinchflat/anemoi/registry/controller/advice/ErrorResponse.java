package dev.pinchflat.anemoi.registry.controller.advice;

import java.util.List;

import dev.pinchflat.anemoi.registry.error.RegistryErrorType;

record ErrorResponse(List<Error> errors) {
	
	public ErrorResponse(RegistryErrorType errorType) {
		this(List.of(new Error(errorType.toString(),errorType.getMessage())));
	}

	record Error(String code, String message){	
	}
	
}
