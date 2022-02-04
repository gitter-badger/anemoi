package dev.pinchflat.anemoi.registry.controller.advice;

import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.pinchflat.anemoi.registry.error.RegistryException;

@ControllerAdvice
public final class ExceptionHandlerAdvice {
	
	@ExceptionHandler(value = RegistryException.class)
	public ResponseEntity<ErrorResponse> handle(@NotNull final RegistryException re) {
		return ResponseEntity//
				.status(re.getRegistryErrorType().getStatus())//
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)//
				.body(new ErrorResponse(re.getRegistryErrorType()));
	}
}
