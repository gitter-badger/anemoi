package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.BlobUploadedResponse;

@Component
public class BlobUploadedReturnValueMapper extends AbstractReturnValueHandler<BlobUploadedResponse> {

	private static final String LOCATION_FORMAT = "\"/v2/%s/blobs/%s";
	
	protected BlobUploadedReturnValueMapper() {
		super(BlobUploadedResponse.class);
	}

	@Override
	public HttpStatus getStatus(BlobUploadedResponse value) {
		return HttpStatus.NO_CONTENT;
	}

	@Override
	public Map<String, String> getHeaders(String path, BlobUploadedResponse value) {
		return Map.of("Range", value.range(), //
				"Content-Length", "0", //
				"Location", String.format(LOCATION_FORMAT, value.repository() ,value.reference()));
	}
}
