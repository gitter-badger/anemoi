package dev.pinchflat.anemoi.registry.controller;

import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.request.GetBlobRequest;

@RestController
public class BlobController {

	@GetMapping(path = "/v2/**/blobs/sha256:*")
	public ResponseEntity<?> getBlob(@NotNull GetBlobRequest getBlobRequest) {
		// @formatter:off
		BodyBuilder bodyBuilder = ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM);
		return getBlobRequest.isCheckBlob() ?
				bodyBuilder.contentLength(0).build(): // TODO: fill content length
				bodyBuilder.body(null); // TODO: fill body 		
		// @formatter:on
		
	}
}
