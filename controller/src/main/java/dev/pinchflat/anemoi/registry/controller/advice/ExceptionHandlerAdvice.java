package dev.pinchflat.anemoi.registry.controller.advice;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.pinchflat.anemoi.registry.controller.response.RegistryErrors;
import dev.pinchflat.anemoi.registry.service.exception.InvalidBlobUploadException;
import dev.pinchflat.anemoi.registry.service.exception.InvalidDigestException;
import dev.pinchflat.anemoi.registry.service.exception.InvalidRepositoryNameException;
import dev.pinchflat.anemoi.registry.service.exception.InvalidTagException;

@ControllerAdvice
final class ExceptionHandlerAdvice {

	@ExceptionHandler(value = InvalidRepositoryNameException.class)
	public ResponseEntity<RegistryErrors> handle(@NotNull final InvalidRepositoryNameException irne) {
		return ResponseEntity. //
				badRequest(). //
				body(RegistryErrors.nameInvalid(irne.getRepositoryName()));
	}
	
	@ExceptionHandler(value = InvalidDigestException.class)
	public ResponseEntity<RegistryErrors> handle(@NotNull final InvalidDigestException ide) {
		return ResponseEntity. //
				badRequest(). //
				body(RegistryErrors.invalidDigest(ide.getDigest()));
	}
	
	@ExceptionHandler(value = InvalidTagException.class)
	public ResponseEntity<RegistryErrors> handle(@NotNull final InvalidTagException ide) {
		return ResponseEntity. //
				badRequest(). //
				body(RegistryErrors.invalidDigest(ide.getTag()));
	}
	
	@ExceptionHandler(value = InvalidBlobUploadException.class)
	public ResponseEntity<RegistryErrors> handle(@NotNull final InvalidBlobUploadException ibue) {
		return ResponseEntity. //
				badRequest(). //
				body(RegistryErrors.invalidBlob(ibue.getUploadId()));
	}
}
