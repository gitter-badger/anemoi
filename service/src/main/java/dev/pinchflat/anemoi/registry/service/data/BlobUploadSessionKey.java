package dev.pinchflat.anemoi.registry.service.data;

import java.util.Objects;

import org.springframework.lang.NonNull;

public class BlobUploadSessionKey {

	@NonNull
	private final String repository;
	
	@NonNull
	private final String uploadId;

	public BlobUploadSessionKey(String repository, String uploadId) {
		super();
		this.repository = repository;
		this.uploadId = uploadId;
	}

	@NonNull
	public String getRepository() {
		return repository;
	}

	@NonNull
	public String getUploadId() {
		return uploadId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(repository, uploadId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlobUploadSessionKey other = (BlobUploadSessionKey) obj;
		return Objects.equals(repository, other.repository) && Objects.equals(uploadId, other.uploadId);
	}
}
