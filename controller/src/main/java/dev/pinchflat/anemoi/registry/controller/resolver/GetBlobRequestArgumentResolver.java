package dev.pinchflat.anemoi.registry.controller.resolver;

import static org.springframework.http.HttpMethod.GET;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.request.GetBlobRequest;
import dev.pinchflat.anemoi.registry.service.data.BlobKey;

@Component
public final class GetBlobRequestArgumentResolver extends AbstractRegexArgumentResolver<GetBlobRequest> {

	@NotNull
	private static final String REGEX = "\\/v2\\/(?<name>.*)\\/blobs\\/(?<digestType>sha256|sha512):(?<digest>[a-z0-9]*)";
	
	public GetBlobRequestArgumentResolver() {
		super(GetBlobRequest.class, REGEX,"name","digestType","digest");
	}

	@Override
	protected GetBlobRequest createObject(HttpServletRequest request, Map<String, String> matchParams)
			throws IOException {
		final BlobKey blobKey = BlobKey.of(matchParams.get("name"), //
				matchParams.get("digestType"), 
				matchParams.get("digest"));
		
		return new GetBlobRequest(//
				GET.toString().equals(request.getMethod()), //
				blobKey);
	}
}
