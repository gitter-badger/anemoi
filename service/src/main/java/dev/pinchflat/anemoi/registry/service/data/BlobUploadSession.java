package dev.pinchflat.anemoi.registry.service.data;

import org.springframework.lang.NonNull;

public class BlobUploadSession {

	@NonNull
	private final BlobUploadSessionKey blobUploadSessionKey;
	
	private long rangeStart;
	private long rangeEnd;
	
	public BlobUploadSession(BlobUploadSessionKey blobUploadSessionKey) {
		this.blobUploadSessionKey = blobUploadSessionKey;
		this.rangeStart = 0;
		this.rangeEnd = 0;
	}

	public BlobUploadSessionKey getBlobUploadSessionKey() {
		return blobUploadSessionKey;
	}

	public long getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(long rangeStart) {
		this.rangeStart = rangeStart;
	}

	public long getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(long rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	
}
