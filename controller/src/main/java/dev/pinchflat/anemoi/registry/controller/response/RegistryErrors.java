package dev.pinchflat.anemoi.registry.controller.response;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

public class RegistryErrors implements Serializable{

	private static final long serialVersionUID = 2338068020953363619L;
	
	@NotEmpty
	private final List<RegistryError> errors;

	private RegistryErrors(RegistryError...errors) {
		super();
		this.errors = List.of(errors);
	}

	public List<RegistryError> getErrors() {
		return errors;
	}
	
	public static RegistryErrors denied() {
		return new RegistryErrors(RegistryError.denied());
	}
	
	public static RegistryErrors unauthorized() {
		return new RegistryErrors(RegistryError.unauthorized());
	}
	
	public static RegistryErrors internalServerError(Serializable detail) {
		return new RegistryErrors(RegistryError.internalServerError(detail));
	}

	public static RegistryErrors nameInvalid(String repositoryName) {
		return new RegistryErrors(RegistryError.nameInvalid(repositoryName));
	}

	public static RegistryErrors invalidDigest(String digest) {
		return new RegistryErrors(RegistryError.invalidDigest(digest));
	}

	public static RegistryErrors unsupported() {
		return new RegistryErrors(RegistryError.unsupported());
	}

	public static RegistryErrors invalidBlob(String uploadId) {
		return new RegistryErrors(RegistryError.invalidBlob(uploadId));
	}
	
}
