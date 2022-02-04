package dev.pinchflat.anemoi.registry.error;

public enum RegistryErrorType {
	BLOB_UNKNOWN(404,"blob unknown to registry"),//
	BLOB_UPLOAD_INVALID(400, "blob upload invalid"),//
	BLOB_UPLOAD_UNKNOWN(404,"blob upload unknown to registry"),//
	DIGEST_INVALID(400,"provided digest did not match uploaded content"), //
	MANIFEST_BLOB_UNKNOWN(404, "blob unknown to registry"), //
	MANIFEST_INVALID(400, "manifest invalid"), //
	MANIFEST_UNKNOWN(404, "manifest unknown"),//
	MANIFEST_UNVERIFIED(400, "manifest failed signature verification"),//
	NAME_INVALID(400, "invalid repository name"), //
	NAME_UNKNOWN(404, "repository name not known to registry"),
	SIZE_INVALID(400, "provided length did not match content length"),//
	TAG_INVALID(400,"manifest tag did not match URI"),//
	UNAUTHORIZED(401,"authentication required"),//
	DENIED(403,"requested access to the resource is denied"),//
	UNSUPPORTED(405, "The operation is unsupported."),//
	INTERNAL_SERVER_ERROR(500,"Internal server error");

	private final int status;

	private final String message;

	private RegistryErrorType(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

}
