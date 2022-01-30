package dev.pinchflat.anemoi.registry.service.data;

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import dev.pinchflat.anemoi.registry.digest.DigestType;

@Validated
public final class BlobKey {
	
	@NonNull
	private final String repository;
	
	@NonNull
	private final DigestType digestType;
	
	@NonNull
	private final String digest;

	protected BlobKey(@NonNull String repository, //
			@NonNull DigestType digestType, //
			@NonNull String digest) {
		super();
		this.repository = repository;
		this.digestType = digestType;
		this.digest = digest;
	}

	public String getRepository() {
		return repository;
	}

	public DigestType getDigestType() {
		return digestType;
	}

	public String getDigest() {
		return digest;
	}

	@Override
	public int hashCode() {
		return Objects.hash(digest, digestType, repository);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlobKey other = (BlobKey) obj;
		return Objects.equals(digest, other.digest) && digestType == other.digestType
				&& Objects.equals(repository, other.repository);
	}
	
	public static BlobKey of(@NonNull String repository, //
			@NonNull String digestType, //
			@NonNull String digest) {
		return new BlobKey(repository, DigestType.fromPrefix(digestType), digest);
		
	}

}
