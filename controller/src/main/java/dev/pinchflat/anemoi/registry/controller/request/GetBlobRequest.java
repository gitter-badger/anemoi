package dev.pinchflat.anemoi.registry.controller.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import dev.pinchflat.anemoi.registry.service.data.BlobKey;

@Validated
public final class GetBlobRequest implements Serializable {

	private static final long serialVersionUID = 2089570254727528775L;

	private final boolean dataRequested;

	@NotNull
	private final BlobKey blobKey;

	public GetBlobRequest(boolean dataRequested, //
			@NotNull BlobKey blobKey) {
		this.dataRequested = dataRequested;
		this.blobKey =blobKey;
	}

	public boolean isDataRequested() {
		return dataRequested;
	}

	public BlobKey getBlobKey() {
		return blobKey;
	}
}
