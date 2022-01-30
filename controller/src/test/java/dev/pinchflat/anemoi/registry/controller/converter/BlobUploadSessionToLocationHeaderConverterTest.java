package dev.pinchflat.anemoi.registry.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import dev.pinchflat.anemoi.registry.service.data.BlobUploadSession;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSessionKey;

class BlobUploadSessionToLocationHeaderConverterTest {

	private final Converter<BlobUploadSession, String> converter = new BlobUploadSessionToLocationHeaderConverter();
	
	@Test
	void testNullBlobKeyConversion() {
		assertEquals(null, converter.convert(null));
	}

	@Test
	void testValidConversion() {
		final String repo = "REPO";
		final String uploadId = "UploadId";
		
		final String expected = "/v2/" + repo + "/blobs/uploads/" + uploadId;

		assertEquals(expected, converter.convert(new BlobUploadSession(new BlobUploadSessionKey(repo, uploadId))));
	}

}
