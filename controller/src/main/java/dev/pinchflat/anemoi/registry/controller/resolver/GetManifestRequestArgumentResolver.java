package dev.pinchflat.anemoi.registry.controller.resolver;

import static org.springframework.http.HttpMethod.GET;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.request.GetManifestRequest;
import dev.pinchflat.anemoi.registry.service.data.ManifestKey;

@Component
public final class GetManifestRequestArgumentResolver extends AbstractRegexArgumentResolver<GetManifestRequest> {

	@NotNull
	private static final String REGEX = "\\/v2\\/(?<name>.*)\\/manifests\\/(?<reference>.*)";

	
	public GetManifestRequestArgumentResolver() {
		super(GetManifestRequest.class, REGEX,"name","reference");
	}

	@Override
	protected GetManifestRequest createObject(HttpServletRequest request, Map<String, String> matchParams)
			throws IOException {
		final ManifestKey manifestKey = ManifestKey .of(matchParams.get("name"), //
				matchParams.get("reference"));
		return new GetManifestRequest(//
				GET.toString().equals(request.getMethod()), //
				manifestKey);
	}
}
