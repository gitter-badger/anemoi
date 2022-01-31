package dev.pinchflat.anemoi.registry.exception;

import org.springframework.lang.NonNull;

public class InvalidRepositoryNameException extends RuntimeException{

	private static final long serialVersionUID = 4827348465725132453L;

	@NonNull
	private final String repositoryName;
	
	public InvalidRepositoryNameException(@NonNull final String repositoryName) {
		super();
		this.repositoryName = repositoryName;
	}

	public String getRepositoryName() {
		return repositoryName;
	}
}
