package dev.pinchflat.anemoi.registry.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.service.ManifestService;

@RestController
public final class ManifestController {

	private static final String LOCATION_FORMAT = "/v2/%s/manifests/%s";
	private final ManifestService manifestService;

	@Autowired
	protected ManifestController(ManifestService manifestService) {
		super();
		this.manifestService = manifestService;
	}

	@GetMapping("/v2/**/manifests/*")
	public ResponseEntity<FileSystemResource>  getManifest(HttpMethod method, Id id) {
		final var manifest = manifestService.get(id);
		var bodyBuilder = ResponseEntity//
				.ok()
				.header("Content-Type", manifest.contentType())
				.header("Docker-Content-Digest", manifest.digest())
				.header("Content-Length", String.valueOf(manifest.contentLength()));
		if (HttpMethod.GET.equals(method)) {
			return bodyBuilder.body(new FileSystemResource(manifest.path()));
		}
		return bodyBuilder.build();
	}
	
	@PutMapping("/v2/**/manifests/*")
	public ResponseEntity<?> putManifest(
			@RequestHeader("Content-Type") String contentType,
			Id id,
			Chunk content) {
		final var manifest = manifestService.write(contentType, id, content);
		return ResponseEntity//
				.created(URI.create(String.format(LOCATION_FORMAT, manifest.id().repository(), manifest.id().reference())))//
				.header("Docker-Content-Digest", manifest.digest())//
				.header("Content-Length", "0")//
				.build();
	}
	
	@DeleteMapping("/v2/**/manifests/*")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteManifest(Id id) {
		manifestService.delete(id);
	}
}
