package dev.pinchflat.anemoi.registry.controller;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.controller.request.BlobUploadRequest;
import dev.pinchflat.anemoi.registry.controller.request.GetBlobRequest;
import dev.pinchflat.anemoi.registry.controller.request.InitiateBlobUploadRequest;
import dev.pinchflat.anemoi.registry.controller.response.RegistryErrors;
import dev.pinchflat.anemoi.registry.service.BlobService;
import dev.pinchflat.anemoi.registry.service.data.Blob;
import dev.pinchflat.anemoi.registry.service.data.BlobKey;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSession;

@RestController
public class BlobController {
	@NotNull
	private final Logger logger = LoggerFactory.getLogger(BlobController.class);

	@NonNull
	private final BlobService blobService;

	@NonNull
	private final Converter<BlobKey, String> locationConverter;
	
	@NonNull
	private final Converter<BlobUploadSession, String> blobUploadSessionLocationConverter;

	@Autowired
	protected BlobController(@NonNull final BlobService blobService, //
			@NonNull @Qualifier("locationHeader") //
			final Converter<BlobKey, String> locationConverter, //
			@NonNull @Qualifier("locationHeader") //
			final Converter<BlobUploadSession, String> blobUploadSessionLocationConverter) {
		super();
		this.blobService = blobService;
		this.locationConverter = locationConverter;
		this.blobUploadSessionLocationConverter = blobUploadSessionLocationConverter;
	}

	@GetMapping("/v2/**/blobs/sha*")
	public void getBlob(@NotNull GetBlobRequest request, //
			@NotNull HttpServletResponse response) throws IOException {
		final Blob blob = blobService.get(request.getBlobKey());
		if (blob.isMountedBlob()) {
			response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
			response.setHeader("Location", locationConverter.convert(blob.getSource().get()));
		} else {
			response.setStatus(HttpStatus.OK.value());
			response.setContentLength(blob.getLength());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			if (request.isDataRequested()) {
				try {
					try (FileInputStream fis = new FileInputStream(blob.getPath().toFile())) {
						IOUtils.copy(fis, response.getOutputStream());
					}
					response.flushBuffer();
				} catch (IOException e) {
					if (response.isCommitted()) {
						logger.trace("Did not write to response since already committed");
						return;
					}
					throw e;
				}
			}
		}
	}
	
	@PostMapping(path="/v2/**/blobs/uploads",params = "!digest")
	public void initiateResumableBlobUpload(@NotNull InitiateBlobUploadRequest request,//
			@NotNull HttpServletResponse response) {
		final BlobUploadSession blobUploadSession = blobService.startUploadSession(request.getRepositoryName());
		response.setStatus(HttpStatus.ACCEPTED.value());
		response.addHeader("Range", "0-0");
		response.addHeader("Content-Length", "0");
		response.addHeader("Blob-Upload-Session-ID", blobUploadSession.getBlobUploadSessionKey().getUploadId());
		response.addHeader("Location", blobUploadSessionLocationConverter.convert(blobUploadSession));
	}
	
	@Deprecated
	@PostMapping(path="/v2/**/blobs/uploads",params = "digest")
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public RegistryErrors initiateMonolithicBlobUpload(@NotNull InitiateBlobUploadRequest request,//
			@NotNull HttpServletResponse response) {
		return RegistryErrors.unsupported();
	}
	
	@PatchMapping(path="/v2/**/blobs/uploads/*")
	public void streamUpload(@NotNull BlobUploadRequest blobUploadRequest,//
			@NotNull HttpServletResponse response) {
		
		final BlobUploadSession blobUploadSession = blobService.appendChunkToUpload(
				blobUploadRequest.getBlobUploadSessionKey(),
				blobUploadRequest.getChunk());
		
		response.setStatus(HttpStatus.ACCEPTED.value());
		response.addHeader("Range", blobUploadSession.getRangeStart()+"-"+blobUploadSession.getRangeEnd());
		response.addHeader("Content-Length", "0");
		response.addHeader("Blob-Upload-Session-ID", blobUploadSession.getBlobUploadSessionKey().getUploadId());
		response.addHeader("Location", blobUploadSessionLocationConverter.convert(blobUploadSession));
	}
	
}
