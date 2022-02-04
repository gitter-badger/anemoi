package dev.pinchflat.anemoi.registry.controller;

import java.net.URI;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.Blob;
import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.UploadSession;
import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryName;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.error.RegistryException;
import dev.pinchflat.anemoi.registry.service.BlobService;

@RestController
public final class BlobController {
	private static final String BLOB_URI_FORMAT = "/v2/%s/blobs/%s";
	private static final String BLOB_UPLOAD_SESSION_URI_FORMAT = "/v2/%s/blobs/uploads/%s";

	@NonNull
	private final BlobService blobService;

	@Autowired
	protected BlobController(@NonNull final BlobService blobService) {
		super();
		this.blobService = blobService;
	}

	@GetMapping("/v2/**/blobs/*")
	public ResponseEntity<FileSystemResource> getBlob(HttpMethod method, Id id) {
		final Blob blob = blobService.get(id);
		;
		BodyBuilder bodyBuilder;
		if (blob.mounted()) {
			bodyBuilder = ResponseEntity//
					.status(HttpStatus.TEMPORARY_REDIRECT)//
					.location(URI.create(String.format(BLOB_URI_FORMAT, blob.source().id().repository(),
							blob.source().id().reference())))//
					.header("Docker-Content-Digest", blob.source().id().reference());
		} else {
			bodyBuilder = ResponseEntity//
					.ok()//
					.header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)//
					.header("Content-Length", String.valueOf(blob.length()))//
					.header("Docker-Content-Digest", blob.id().reference());
		}
		if (HttpMethod.GET.equals(method)) {
			return bodyBuilder.body(new FileSystemResource(blob.path()));
		}
		return bodyBuilder.build();
	}

	@DeleteMapping("/v2/**/blobs/*")
	public ResponseEntity<?> deleteBlob(@NotNull Id id) {
		Blob blob = blobService.delete(id);
		return ResponseEntity//
				.accepted().header("Content-Length", "0").header("Docker-Content-Digest", blob.id().reference())//
				.build();
	}

	@PostMapping(path = "/v2/**/blobs/uploads")
	public ResponseEntity<?> startUploadSession(//
			@RepositoryName String repositoryName, //
			@RequestParam("digest") Optional<String> digest, //
			@RequestParam("mount") Optional<String> reference, //
			@RequestParam("from") Optional<String> repository) {
		if (digest.isPresent()) {
			throw new RegistryException(RegistryErrorType.UNSUPPORTED);
		}
		if (reference.isPresent()) {
			final Id sourceId = new Id(repository.get(), reference.get());
			final Blob blob = blobService.mount(repositoryName, sourceId);
			return ResponseEntity//
					.created(URI.create(String.format(BLOB_URI_FORMAT, blob.id().repository(), blob.id().reference())))//
					.header("Blob-Upload-Session-ID", blob.id().reference())//
					.header("Content-Length", "0")//
					.build();
		} else {
			final UploadSession session = blobService.startUploadSession(repositoryName);
			return ResponseEntity//
					.accepted()//
					.header("Content-Length", "0")//
					.header("Range", session.range())//
					.header("Blob-Upload-Session-ID", session.id().reference())//
					.header("Location",
							String.format(BLOB_UPLOAD_SESSION_URI_FORMAT, session.id().repository(),
									session.id().reference()))//
					.build();
		}
	}

	@GetMapping(path = "/v2/**/blobs/uploads/*")
	public ResponseEntity<?> getBlobUpload(Id id) {
		final UploadSession session = blobService.getUploadStatus(id);
		return ResponseEntity//
				.noContent()//
				.header("Content-Length", "0")//
				.header("Range", session.range())//
				.header("Blob-Upload-Session-ID", session.id().reference())//
				.build();
	}
	
	@PatchMapping(path = "/v2/**/blobs/uploads/*")
	public ResponseEntity<?> streamUpload(Id id, Chunk chunk) {
		final UploadSession session = blobService.writeChunk(id, chunk);
		return ResponseEntity//
				.accepted()//
				.header("Content-Length", "0")//
				.header("Range", session.range())//
				.header("Blob-Upload-Session-ID", session.id().reference())//
				.header("Location",
						String.format(BLOB_UPLOAD_SESSION_URI_FORMAT, session.id().repository(),
								session.id().reference()))//
				.build();
	}

	@PutMapping(path = "/v2/**/blobs/uploads/*")
	public ResponseEntity<?> blobUpload(Id id, Chunk chunk, @RequestParam(name = "digest") String digest) {
		final Blob blob = blobService.endUploadSession(id, chunk, digest);
		return ResponseEntity//
				.noContent()//
				.header("Content-Length", "0")//
				.header("Location", String.format(BLOB_URI_FORMAT, blob.id().repository(),blob.id().reference()))//
				.build();
	}
}
