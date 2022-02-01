package dev.pinchflat.anemoi.registry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.Manifest;
import dev.pinchflat.anemoi.registry.service.ManifestService;

@RestController
final class ManifestController {

	private final ManifestService manifestService;

	@Autowired
	protected ManifestController(ManifestService manifestService) {
		super();
		this.manifestService = manifestService;
	}

	@GetMapping("/v2/**/manifests/*")
	public Manifest getManifest(Id id) {
		return manifestService.get(id);
	}
	
	@PutMapping("/v2/**/manifests/*")
	public Manifest putManifest(
			@RequestHeader("Content-Type") String contentType,
			Id id,
			Chunk content) {
		return manifestService.write(contentType, id, content);
	}
	
	@DeleteMapping("/v2/**/manifests/*")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteManifest(Id id) {
		manifestService.delete(id);
	}
}
