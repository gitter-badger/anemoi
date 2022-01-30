package dev.pinchflat.anemoi.registry.service.exception;

import org.springframework.lang.NonNull;

public class InvalidTagException extends RuntimeException{

	private static final long serialVersionUID = 3166026275778041213L;
	
	@NonNull
	private final String tag;
	
	protected InvalidTagException(@NonNull final String tag) {
		super();
		this.tag = tag;
	}

	@NonNull
	public String getTag() {
		return tag;
	}
}
