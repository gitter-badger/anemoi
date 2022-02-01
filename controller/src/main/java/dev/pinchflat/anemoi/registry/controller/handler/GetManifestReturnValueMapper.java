package dev.pinchflat.anemoi.registry.controller.handler;

import java.nio.file.Path;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.GetManifestResponse;

@Component
public class GetManifestReturnValueMapper extends AbstractReturnValueHandler<GetManifestResponse> {

	protected GetManifestReturnValueMapper() {
		super(GetManifestResponse.class);
	}
	
	@Override
	public HttpStatus getStatus(GetManifestResponse value) {
		return HttpStatus.OK;
	}

	@Override
	public Map<String, String> getHeaders(String path, GetManifestResponse value) {
		return Map.of( "Content-Type", value.contentType());
	}
	
	@Override
	public Path getResource(String method, GetManifestResponse value) {
		return "GET".equals(method) ? value.path() : null;   
	}
}
