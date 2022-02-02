package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.BlobMountedResponse;

@Component
public class BlobMountedReturnValueMapper extends AbstractReturnValueHandler<BlobMountedResponse> {

	private static final String LOCATION_FORMAT = "/v2/%s/blobs/%s";

	protected BlobMountedReturnValueMapper() {
		super(BlobMountedResponse.class);
	}

	@Override
	public HttpStatus getStatus(BlobMountedResponse value) {
		return HttpStatus.CREATED;
	}

	@Override
	public Map<String, String> getHeaders(String path, BlobMountedResponse value) {
		return Map.of("Content-Length", "0", //
				"Location", String.format(LOCATION_FORMAT, value.repository(), value.reference()));
	}
}
