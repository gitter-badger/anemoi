package dev.pinchflat.anemoi.registry.service;

import java.nio.file.Path;

import org.springframework.lang.NonNull;

import dev.pinchflat.anemoi.registry.service.data.Blob;
import dev.pinchflat.anemoi.registry.service.data.BlobKey;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSession;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSessionKey;

public interface BlobService {

	// TODO: throws InvalidDigestException and Invalid Repository Name Exception
	@NonNull
	Blob get(@NonNull BlobKey blobKey);
	
	// TODO: throws Invalid Repository Name Exception
	// TODO: Implement Access Denied
	@NonNull
	BlobUploadSession startUploadSession(String repositoryName);
	
	// TODO: throws Invalid Repository Name Exception 
	//InvalidBlobUploadException
	@NonNull
	BlobUploadSession appendChunkToUpload(BlobUploadSessionKey blobUploadSessionKey, Path chunk);

}
