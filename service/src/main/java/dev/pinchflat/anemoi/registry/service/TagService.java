package dev.pinchflat.anemoi.registry.service;

import org.springframework.lang.NonNull;

public interface TagService {

	// NAME_UNKNOWN,DENIED
	@NonNull
	TagList list(String repositoryName, long startAfter, long pageCount);

}
