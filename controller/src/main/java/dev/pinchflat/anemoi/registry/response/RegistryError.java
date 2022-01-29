package dev.pinchflat.anemoi.registry.response;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

public class RegistryError implements Serializable{

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
}
