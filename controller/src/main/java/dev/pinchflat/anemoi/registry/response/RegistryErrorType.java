package dev.pinchflat.anemoi.registry.response;

import javax.validation.constraints.NotNull;

public enum RegistryErrorType {
	BLOB_UNKNOW("blob unknown to registry"),
	BLOB_UPLOAD_INVALID("blob upload invalid"),
	BLOB_UPLOAD_UNKNOWN("blob upload unknown to registry"),
	DIGEST_INVALID("provided digest did not match uploaded content"),
	MANIFEST_BLOB_UNKNOWN("blob unknown to registry"),
	MANIFEST_INVALID("manifest invalid"),
	MANIFEST_UNKNOWN("manifest unknown"),
	MANIFEST_UNVERIFIED("manifest failed signature verification"),
	NAME_INVALID("invalid repository name"),
	NAME_UNKNOWN("repository name not known to registry"),
	SIZE_INVALID("provided length did not match content length"),
	TAG_INVALID("manifest tag did not match URI"),
	UNAUTHORIZED("authentication required"),
	DENIED("requested access to the resource is denied"),
	UNSUPPORTED("The operation is unsupported.");
	
	
	@NotNull
	private final String message;

	private RegistryErrorType(@NotNull String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
}
