package dev.pinchflat.anemoi.registry.controller.response;

import java.util.List;

public record GetTagsResponse(String repositoryName, long start, long count, long total, List<String> tags) {

	public long nextPageOffset() {
		return start+(count-1);
	}
	
	public boolean hasNext() {
		return nextPageOffset() < (total-1);
	}
	
}
