package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.UploadSessionResponse;

@Component
public class UploadSessionReturnValueMapper extends AbstractReturnValueHandler<UploadSessionResponse> {
	
	private static final String LOCATION_FORMAT = "/v2/%s/blobs/uploads/%s";

	protected UploadSessionReturnValueMapper() {
		super(UploadSessionResponse.class);
	}

	@Override
	public HttpStatus getStatus(UploadSessionResponse value) {
		return HttpStatus.ACCEPTED;
	}

	@Override
	public Map<String, String> getHeaders(String path, UploadSessionResponse value) {
		return Map.of( //
				"Content-Length", "0", //
				"Range", value.range(), //
				"Blob-Upload-Session-ID", value.reference(), //
				"Location", String.format(LOCATION_FORMAT, value.repository(), value.reference()));
	}
}
