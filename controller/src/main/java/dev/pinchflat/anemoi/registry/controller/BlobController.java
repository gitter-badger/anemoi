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
import dev.pinchflat.anemoi.registry.controller.response.BlobMountedResponse;
import dev.pinchflat.anemoi.registry.controller.response.BlobUploadedResponse;
import dev.pinchflat.anemoi.registry.controller.response.GetBlobResponse;
import dev.pinchflat.anemoi.registry.controller.response.RegistryErrorResponse;
import dev.pinchflat.anemoi.registry.controller.response.UploadSessionResponse;
import dev.pinchflat.anemoi.registry.error.RegistryError;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.service.BlobService;

@RestController
final class BlobController {

	@NonNull
	private final BlobService blobService;

	@Autowired
	protected BlobController(@NonNull final BlobService blobService) {
		super();
		this.blobService = blobService;
	}

	@GetMapping("/v2/**/blobs/sha*")
	public GetBlobResponse getBlob(@NotNull Id id) {
		final Blob blob = blobService.get(id);
		final Id responseId = blob.mounted() ? blob.source().id() : blob.id();
		return new GetBlobResponse(responseId.repository(),responseId.reference(),blob.length(),blob.path(),blob.mounted());
	}
	
	@DeleteMapping("/v2/**/blobs/sha*")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteBlob(@NotNull Id id) {
		blobService.delete(id);
	}

	@Deprecated
	@PostMapping(path = "/v2/**/blobs/uploads", params = "digest")
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public RegistryErrorResponse initiateMonolithicBlobUpload(@RepositoryName String repositoryName) {
		return new RegistryErrorResponse(new RegistryError(RegistryErrorType.UNSUPPORTED));
	}

	@PostMapping(path = "/v2/**/blobs/uploads", params = {"!digest","!mount","!from"})
	public UploadSessionResponse startUploadSession(@RepositoryName String repositoryName) {
		final UploadSession session = blobService.startUploadSession(repositoryName);
		return new UploadSessionResponse(session.id().repository(), session.id().reference(),session.range());
	}
	
	@PatchMapping(path = "/v2/**/blobs/uploads/*")
	public UploadSessionResponse streamUpload(Id id, Chunk chunk) {
		final UploadSession session = blobService.writeChunk(id, chunk);
		return new UploadSessionResponse(session.id().repository(), session.id().reference(),session.range());
	}

	@PutMapping(path = "/v2/**/blobs/uploads/*")
	public BlobUploadedResponse blobUpload(Id id, Chunk chunk,
			@RequestParam(name = "digest") String digest) {
		final Blob blob = blobService.endUploadSession(id, chunk, digest);
		return new BlobUploadedResponse(blob.id().repository(),blob.id().reference(), blob.range());
	}
	
	@PostMapping(path = "/v2/**/blobs/uploads", params = {"mount","from"})
	public BlobMountedResponse mountBlob(
			@RepositoryName String repositoryName,
			@RequestParam("mount") String reference,
			@RequestParam("from") String repository) {
		final Id sourceId = new Id(repository,reference);
		final Blob blob = blobService.mount(repositoryName,sourceId);
		return new BlobMountedResponse(blob.id().repository(),blob.id().reference());
	}


}
