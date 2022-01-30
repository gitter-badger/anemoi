package dev.pinchflat.anemoi.registry.service.data;

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
public final class ManifestKey {
	
	@NonNull
	private final String repository;
	
	@NonNull
	private final String reference;

	protected ManifestKey(@NonNull String repository, //
			@NonNull String reference) {
		super();
		this.repository = repository;
		this.reference= reference;
	}

	public String getRepository() {
		return repository;
	}
	
	public String getReference() {
		return reference;
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(reference, repository);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManifestKey other = (ManifestKey) obj;
		return Objects.equals(reference, other.reference) && Objects.equals(repository, other.repository);
	}

	public static ManifestKey of(@NonNull String repository, //
			@NonNull String reference) {
		return new ManifestKey(repository, reference);
		
	}

}
