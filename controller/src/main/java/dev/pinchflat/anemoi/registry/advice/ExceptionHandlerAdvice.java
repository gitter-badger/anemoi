package dev.pinchflat.anemoi.registry.advice;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.pinchflat.anemoi.registry.exception.InvalidRequestPathMappingException;
import dev.pinchflat.anemoi.registry.response.RegistryErrors;

@ControllerAdvice
final class ExceptionHandlerAdvice {

	@ExceptionHandler(value = InvalidRequestPathMappingException.class)
    public ResponseEntity<RegistryErrors> handle(@NotNull final InvalidRequestPathMappingException irpme) {
		//@formatter:off
		return ResponseEntity.
			   internalServerError().
			   body(RegistryErrors.internalServerError(irpme));
		//@formatter:on
    }
}
