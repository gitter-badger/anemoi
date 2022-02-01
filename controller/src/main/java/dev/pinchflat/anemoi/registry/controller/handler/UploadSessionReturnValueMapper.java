package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.UploadSession;

@Component
public class UploadSessionReturnValueMapper extends AbstractReturnValueHandler<UploadSession> {
	
	private static final String LOCATION_FORMAT = "\"/v2/%s/blobs/uploads/%s";

	protected UploadSessionReturnValueMapper() {
		super(UploadSession.class);
	}

	@Override
	public HttpStatus getStatus(UploadSession value) {
		return HttpStatus.ACCEPTED;
	}

	@Override
	public Map<String, String> getHeaders(String path, UploadSession value) {
		return Map.of( //
				"Content-Length", "0", //
				"Range", value.range(), //
				"Blob-Upload-Session-ID", value.id().reference(), //
				"Location", String.format(LOCATION_FORMAT, value.id().repository(), value.id().reference()));
	}
}
