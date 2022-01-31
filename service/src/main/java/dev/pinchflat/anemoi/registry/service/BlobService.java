package dev.pinchflat.anemoi.registry.service;

import javax.validation.constraints.NotNull;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import dev.pinchflat.anemoi.registry.Blob;
import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.UploadSession;

@Validated
public interface BlobService {

	// TODO: InvalidDigestException 
	// TODO: Invalid Repository Name Exception
	@NonNull
	Blob get(@NonNull Id id);

	// DIGEST_INVALID,NAME_INVALID,NAME_UNKNOWN,BLOB_UNKNOWN,UNSUPPORTED,NAME_UNKNOWN,DENIED
	void delete(@NonNull Id id);
	
	// TODO: throws Invalid Repository Name Exception
	// TODO: Implement Access Denied
	@NonNull
	UploadSession startUploadSession(@NonNull String repositoryName);

	// TODO: throws Invalid Repository Name Exception
	// TODO: InvalidBlobUploadException
	@NonNull
	UploadSession writeChunk(@NonNull Id id, @NonNull Chunk chunk);

	// TODO: throws InvalidDigestException
	// TODO: InvalidRepositoryNameException
	// TODO: InvalidBlobUploadException
	@NonNull
	Blob endUploadSession(@NonNull Id id, //
			@Nullable Chunk chunk,//
			@NonNull String digest);

	// DIGEST_INVALID,NAME_INVALID,UNSUPPORTED
	@NonNull
	Blob mount(@NonNull String repositoryName, @NonNull Id sourceId);
}
