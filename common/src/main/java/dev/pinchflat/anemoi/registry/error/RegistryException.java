package dev.pinchflat.anemoi.registry.error;

public class RegistryException extends RuntimeException {
	
	private static final long serialVersionUID = 9167015754951097817L;
	
	private final RegistryErrorType registryErrorType;

	public RegistryException(RegistryErrorType registryErrorType) {
		super();
		this.registryErrorType = registryErrorType;
	}

	public RegistryErrorType getRegistryErrorType() {
		return registryErrorType;
	}
}
