package dev.pinchflat.anemoi.registry.controller.request;

import org.springframework.lang.NonNull;

public class InitiateBlobUploadRequest {
	
	@NonNull
	private final String repositoryName;

	public InitiateBlobUploadRequest(@NonNull String repositoryName) {
		super();
		this.repositoryName = repositoryName;
	}
	
	@NonNull
	public String getRepositoryName() {
		return repositoryName;
	}

	
}
