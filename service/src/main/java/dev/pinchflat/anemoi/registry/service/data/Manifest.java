package dev.pinchflat.anemoi.registry.service.data;

import java.nio.file.Path;

import org.springframework.lang.NonNull;

public final class Manifest {

	@NonNull
	private final ManifestKey key;

	@NonNull
	private final Path path;
	
	@NonNull
	private final String mediaType;

	public Manifest(ManifestKey key, Path path, String mediaType) {
		super();
		this.key = key;
		this.path = path;
		this.mediaType = mediaType;
	}

	@NonNull
	public ManifestKey getKey() {
		return key;
	}

	@NonNull
	public Path getPath() {
		return path;
	}

	@NonNull
	public String getMediaType() {
		return mediaType;
	}

	
}
