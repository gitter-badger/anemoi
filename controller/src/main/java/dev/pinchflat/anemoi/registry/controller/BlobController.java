package dev.pinchflat.anemoi.registry.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.Blob;
import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.UploadSession;
import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryName;
import dev.pinchflat.anemoi.registry.error.RegistryError;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.service.BlobService;

@RestController
public class BlobController {

	@NonNull
	private final BlobService blobService;

	@Autowired
	protected BlobController(@NonNull final BlobService blobService) {
		super();
		this.blobService = blobService;
	}

	@GetMapping("/v2/**/blobs/sha*")
	public Blob getBlob(@NotNull Id id) {
		return blobService.get(id);
	}
	
	@DeleteMapping("/v2/**/blobs/sha*")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteBlob(@NotNull Id id) {
		blobService.delete(id);
	}

	@PostMapping(path = "/v2/**/blobs/uploads", params = {"!digest","!mount","!from"})
	public UploadSession initiateResumableBlobUpload(@RepositoryName String repositoryName) {
		return blobService.startUploadSession(repositoryName);
	}
	
	@PostMapping(path = "/v2/**/blobs/uploads", params = {"mount","from"})
	public Blob mountBlob(
			@RepositoryName String repositoryName,
			@RequestParam("mount") String reference,
			@RequestParam("from") String repository) {
		final Id sourceId = new Id(repository,reference);
		return blobService.mount(repositoryName,sourceId);
	}

	@Deprecated
	@PostMapping(path = "/v2/**/blobs/uploads", params = "digest")
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public RegistryError initiateMonolithicBlobUpload(@RepositoryName String repositoryName) {
		return new RegistryError(RegistryErrorType.UNSUPPORTED);
	}

	@PatchMapping(path = "/v2/**/blobs/uploads/*")
	public UploadSession streamUpload(Id id, Chunk chunk) {
		return blobService.writeChunk(id, chunk);
	}

	@PutMapping(path = "/v2/**/blobs/uploads/*")
	public Blob blobUpload(Id id, Chunk chunk,
			@RequestParam(name = "digest") String digest) {
		return blobService.endUploadSession(id, chunk, digest);
	}

}
