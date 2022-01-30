package dev.pinchflat.anemoi.registry.service;

import org.springframework.lang.NonNull;

import dev.pinchflat.anemoi.registry.service.data.Manifest;
import dev.pinchflat.anemoi.registry.service.data.ManifestKey;

public interface ManifestService {

	// TODO: throws InvalidDigestException and InvalidTagException
	@NonNull
	Manifest get(@NonNull ManifestKey manifestKey);

}
