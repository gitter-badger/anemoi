package dev.pinchflat.anemoi.registry.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import dev.pinchflat.anemoi.registry.digest.DigestType;

@Validated
public final class GetBlobRequest implements Serializable {

	private static final long serialVersionUID = 2089570254727528775L;

	private final boolean checkBlob;

	@NotNull
	private final String repositoryName;

	@NotNull
	private final DigestType digestType;

	@NotNull
	private final String digest;

	public GetBlobRequest(// @formatter:off
			boolean checkBlob, 
			@NotNull String repositoryName, 
			@NotNull DigestType digestType,
			@NotNull String digest// @formatter:on
	) {
		this.checkBlob = checkBlob;
		this.repositoryName = repositoryName;
		this.digestType = digestType;
		this.digest = digest;
	}

	public boolean isCheckBlob() {
		return checkBlob;
	}

	@NotNull
	public String getRepositoryName() {
		return repositoryName;
	}

	@NotNull
	public DigestType getDigestType() {
		return digestType;
	}

	@NotNull
	public String getDigest() {
		return digest;
	}
}
