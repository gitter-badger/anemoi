package dev.pinchflat.anemoi.registry.service.data;

import java.nio.file.Path;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public final class Blob {

	@NonNull
	private final BlobKey key;

	private int length;

	private Optional<BlobKey> source;

	@Nullable
	private Path path;

	public Blob(final BlobKey key) {
		this.key = key;
		this.length = 0;
		this.source = Optional.empty();
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Optional<BlobKey> getSource() {
		return source;
	}

	public void setSource(Optional<BlobKey> source) {
		this.source = source;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public BlobKey getKey() {
		return key;
	}
	
	public boolean isMountedBlob() {
		return this.source.isPresent();
	}
}
