package dev.pinchflat.anemoi.registry.controller.resolver;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.request.InitiateBlobUploadRequest;

@Component
public class InitiateBlobUploadRequestArgumentResolver
		extends AbstractRegexArgumentResolver<InitiateBlobUploadRequest> {

	private static final String REGEX = "\\/v2\\/(?<name>.*)\\/blobs\\/uploads";

	public InitiateBlobUploadRequestArgumentResolver() {
		super(InitiateBlobUploadRequest.class, REGEX,"name");
	}

@Override
	protected InitiateBlobUploadRequest createObject(HttpServletRequest request, Map<String, String> matchParams)
			throws IOException {
		return new InitiateBlobUploadRequest(matchParams.get("name"));
	}

}
