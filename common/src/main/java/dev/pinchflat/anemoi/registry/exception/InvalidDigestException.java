package dev.pinchflat.anemoi.registry.exception;

import org.springframework.lang.NonNull;

public class InvalidDigestException extends RuntimeException{

	private static final long serialVersionUID = -7296251790662110404L;
	
	@NonNull
	private final String digest;
	
	public InvalidDigestException(@NonNull final String digest) {
		super();
		this.digest = digest;
	}

	public String getDigest() {
		return digest;
	}


}
