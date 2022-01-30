package dev.pinchflat.anemoi.registry.controller.response;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

public class RegistryError implements Serializable {

	private static final long serialVersionUID = -1168952581645576223L;

	@NotNull
	private final String code;

	@NotNull
	private final String message;

	@Nullable
	private final Serializable detail;

	private RegistryError(@NotNull RegistryErrorType registryErrorType) {
		this.code = registryErrorType.toString();
		this.message = registryErrorType.getMessage();
		this.detail = null;
	}

	private RegistryError(@NotNull RegistryErrorType registryErrorType, @NotNull Serializable detail) {
		this.code = registryErrorType.toString();
		this.message = registryErrorType.getMessage();
		this.detail = detail;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Serializable getDetail() {
		return detail;
	}

	public static RegistryError denied() {
		return new RegistryError(RegistryErrorType.DENIED);
	}

	public static RegistryError unauthorized() {
		return new RegistryError(RegistryErrorType.UNAUTHORIZED);
	}

	public static RegistryError internalServerError(Serializable detail) {
		return new RegistryError(RegistryErrorType.INTERNAL_SERVER_ERROR, detail);
	}

	public static RegistryError nameInvalid(String repositoryName) {
		return new RegistryError(RegistryErrorType.NAME_INVALID, repositoryName);
	}

	public static RegistryError invalidDigest(String digest) {
		return new RegistryError(RegistryErrorType.DIGEST_INVALID, digest);
	}

	public static RegistryError unsupported() {
		return new RegistryError(RegistryErrorType.UNSUPPORTED);
	}

	public static RegistryError invalidBlob(String uploadId) {
		return new RegistryError(RegistryErrorType.BLOB_UPLOAD_INVALID,uploadId);
	}
}
