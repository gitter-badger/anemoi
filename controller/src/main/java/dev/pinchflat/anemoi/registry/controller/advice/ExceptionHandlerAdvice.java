package dev.pinchflat.anemoi.registry.controller.advice;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.pinchflat.anemoi.registry.controller.response.RegistryErrorResponse;
import dev.pinchflat.anemoi.registry.error.RegistryError;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.exception.InvalidBlobUploadException;
import dev.pinchflat.anemoi.registry.exception.InvalidDigestException;
import dev.pinchflat.anemoi.registry.exception.InvalidRepositoryNameException;
import dev.pinchflat.anemoi.registry.exception.InvalidTagException;

@ControllerAdvice
final class ExceptionHandlerAdvice {

	@ExceptionHandler(value = InvalidRepositoryNameException.class)
	public RegistryErrorResponse handle(@NotNull final InvalidRepositoryNameException irne) {
		return new RegistryErrorResponse(new RegistryError(RegistryErrorType.NAME_INVALID, irne.getRepositoryName()));
	}
	
	@ExceptionHandler(value = InvalidDigestException.class)
	public RegistryErrorResponse handle(@NotNull final InvalidDigestException ide) {
		return new RegistryErrorResponse(new RegistryError(RegistryErrorType.DIGEST_INVALID, ide.getDigest()));
	}
	
	@ExceptionHandler(value = InvalidTagException.class)
	public RegistryErrorResponse handle(@NotNull final InvalidTagException ide) {
		return new RegistryErrorResponse(new RegistryError(RegistryErrorType.TAG_INVALID, ide.getTag()));
	}
	
	@ExceptionHandler(value = InvalidBlobUploadException.class)
	public RegistryErrorResponse handle(@NotNull final InvalidBlobUploadException ibue) {
		return new RegistryErrorResponse(new RegistryError(RegistryErrorType.BLOB_UPLOAD_INVALID, ibue.getUploadId()));
	}
}
