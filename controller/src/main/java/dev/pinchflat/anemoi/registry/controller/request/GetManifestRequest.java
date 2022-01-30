package dev.pinchflat.anemoi.registry.controller.request;

import java.io.Serializable;

import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import dev.pinchflat.anemoi.registry.service.data.ManifestKey;

@Validated
public final class GetManifestRequest implements Serializable {

	private static final long serialVersionUID = 4575278069337807122L;

	private final boolean dataRequested;

	@NonNull
	private final ManifestKey manifestKey;

	public GetManifestRequest(boolean dataRequested, //
			@NonNull ManifestKey manifestKey) {
		this.dataRequested = dataRequested;
		this.manifestKey =manifestKey;
	}

	public boolean isDataRequested() {
		return dataRequested;
	}

	public ManifestKey getManifestKey() {
		return manifestKey;
	}

	
}
