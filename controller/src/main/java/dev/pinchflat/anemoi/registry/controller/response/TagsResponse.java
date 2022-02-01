package dev.pinchflat.anemoi.registry.controller.response;

import java.util.List;

public record TagsResponse(String repositoryName, long start, long count, long total, List<String> tags) {

	public long nextPageOffset() {
		return start+(count-1);
	}
	
	public boolean hasNext() {
		if (start >= 0 && count > 0) {
			if (nextPageOffset() < (total-1)) {
				return true;
			}
		}
		return false;
	}
	
}
