package dev.pinchflat.anemoi.registry.service;

import org.springframework.lang.NonNull;

import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.Manifest;

public interface ManifestService {

	// TODO: throws InvalidRepositoryName, InvalidDigestException and InvalidTagException
	@NonNull
	Manifest get(@NonNull Id id);

	//TODO: throws InvalidRepositoryName, InvalidTagException and InvalidManifestException
	// TODO: ManifestUnverified, BlobUnknown
	@NonNull
	Manifest write(@NonNull String contentType, @NonNull Id id, @NonNull Chunk chunk);

	//Delete the manifest identified by name and reference. Note that a manifest can only be deleted by digest.
	// TODO: throws InvalidRepositoryName, NAME_UNKNOWN,,MANIFEST_UNKNOWN,DENIED and InvalidTagException
	void delete(@NonNull Id id);

}
