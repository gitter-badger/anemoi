package dev.pinchflat.anemoi.registry.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;

public interface TagService {

	// NAME_UNKNOWN,DENIED
	@NonNull
	Pair<Long, List<String>> list(String repositoryName, long startAfter, long pageCount);

}
