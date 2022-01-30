package dev.pinchflat.anemoi.registry.controller.resolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.request.BlobUploadRequest;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSessionKey;

@Component
public class BlobUploadRequestArgumentResolver
		extends AbstractRegexArgumentResolver<BlobUploadRequest> {

	private static final String REGEX = "\\/v2\\/(?<name>.*)\\/blobs\\/uploads\\/(?<uploadId>.*)";

	public BlobUploadRequestArgumentResolver() {
		super(BlobUploadRequest.class, REGEX,"name","uploadId");
	}

	@Override
	protected BlobUploadRequest createObject(HttpServletRequest request, Map<String, String> matchParams) throws IOException {
		BlobUploadSessionKey busk = new BlobUploadSessionKey(matchParams.get("name"),matchParams.get("uploadId"));
		File f = File.createTempFile("prm", "test");
		int size = 0;
		try(FileOutputStream fos = new FileOutputStream(f)){
			size = IOUtils.copy(request.getInputStream(), fos);
			fos.flush();
		}
		return new BlobUploadRequest(busk, size != 0 ? f.toPath():null, request.getParameter("digest"));
	}

}
