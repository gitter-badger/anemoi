package dev.pinchflat.anemoi.registry.controller.handler;

import java.nio.file.Path;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.GetBlobResponse;

@Component
public class GetBlobReturnValueMapper extends AbstractReturnValueHandler<GetBlobResponse> {
	private static final String LOCATION_FORMAT = "\"/v2/%s/blobs/%s";
	
	protected GetBlobReturnValueMapper() {
		super(GetBlobResponse.class);
	}

	@Override
	public HttpStatus getStatus(GetBlobResponse value) {
		return value.mounted() ? HttpStatus.TEMPORARY_REDIRECT : HttpStatus.OK;
	}

	@Override
	public Map<String, String> getHeaders(String path, GetBlobResponse value) {
		return value.mounted() ? //
				Map.of("Location",String.format(LOCATION_FORMAT, value.repository() , value.reference())) : //
				Map.of("Content-Length", String.valueOf(value.length()), //
						"Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	@Override
	public Path getResource(String method, GetBlobResponse value) {
		return value.mounted() && "GET".equals(method) ? value.path() : null;
	}
}
