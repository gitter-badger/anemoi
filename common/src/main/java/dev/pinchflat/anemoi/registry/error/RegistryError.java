package dev.pinchflat.anemoi.registry.error;

public record RegistryError(RegistryErrorType type, Object detail) {
	public RegistryError(RegistryErrorType type) {
		this(type, null);
	}
}
