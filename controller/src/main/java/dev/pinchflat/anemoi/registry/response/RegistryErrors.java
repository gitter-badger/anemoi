package dev.pinchflat.anemoi.registry.response;

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
	
}
