package dev.pinchflat.anemoi.registry.controller.request;

import java.io.Serializable;
import java.nio.file.Path;

import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import dev.pinchflat.anemoi.registry.service.data.BlobUploadSessionKey;

@Validated
public final class BlobUploadRequest implements Serializable {

	private static final long serialVersionUID = -1752117350054044141L;

	private final BlobUploadSessionKey blobUploadSessionKey;

	@Nullable
	private final Path chunk;
	
	@Nullable
	private final String digest;

	
	public BlobUploadRequest(BlobUploadSessionKey blobUploadSessionKey, Path chunk,String digest) {
		super();
		this.blobUploadSessionKey = blobUploadSessionKey;
		this.chunk = chunk;
		this.digest = digest;
	}


	public BlobUploadSessionKey getBlobUploadSessionKey() {
		return blobUploadSessionKey;
	}


	public Path getChunk() {
		return chunk;
	}


	public String getDigest() {
		return digest;
	}
	
}
