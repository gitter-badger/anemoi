package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.PutManifestResponse;

@Component
public class PutManifestReturnValueMapper extends AbstractReturnValueHandler<PutManifestResponse> {
	
	private static final String LOCATION_FORMAT = "\"/v2/%s/returnValue/%s";

	protected PutManifestReturnValueMapper() {
		super(PutManifestResponse.class);
	}

	@Override
	public HttpStatus getStatus(PutManifestResponse value) {
		return HttpStatus.CREATED;
	}

	@Override
	public Map<String, String> getHeaders(String path, PutManifestResponse value) {
		return Map.of( //
				"Content-Length", "0", //
				"Location", String.format(LOCATION_FORMAT, value.repository(), value.reference()), //
				"Docker-Content-Digest", value.digest());
	}
}
