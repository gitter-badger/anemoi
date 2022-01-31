package dev.pinchflat.anemoi.registry.exception;

import org.springframework.lang.NonNull;

public class InvalidBlobUploadException extends RuntimeException{

	private static final long serialVersionUID = -8299101185178763750L;
	
	@NonNull
	private final String uploadId;
	
	public InvalidBlobUploadException(@NonNull final String uploadId) {
		super();
		this.uploadId = uploadId;
	}

	public String getUploadId() {
		return uploadId;
	}

	
}
