package dev.pinchflat.anemoi.registry.digest;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

public enum DigestType {
	SHA256("sha256"),
	SHA512("sha512");

	@NotNull
	private final String prefix;
	
	private DigestType(@NotNull String prefix) {
		this.prefix = prefix;
	}	

	public String getPrefix() {
		return prefix;
	}
	
	@NotNull
	public static DigestType fromPrefix(@NotNull String prefix) {
		// @formatter:off
		return Arrays.stream(DigestType.values())
				.filter(digestType -> digestType.getPrefix().equals(prefix))
				.findFirst()
				.orElse(DigestType.SHA256);
		// @formatter: on
	}
}
