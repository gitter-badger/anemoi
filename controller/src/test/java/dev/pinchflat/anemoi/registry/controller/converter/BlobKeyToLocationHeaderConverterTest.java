package dev.pinchflat.anemoi.registry.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.convert.converter.Converter;

import dev.pinchflat.anemoi.registry.service.data.BlobKey;

class BlobKeyToLocationHeaderConverterTest {

	private final Converter<BlobKey, String> converter = new BlobKeyToLocationHeaderConverter();

	@Test
	void testNullBlobKeyConversion() {
		assertEquals(null, converter.convert(null));
	}

	@ParameterizedTest
	@CsvSource(value = { "repo,sha256,01231321231",//
			"namespace/repo,sha256,01231321231",//
			"namespace/repo/tag,sha256,01231321231" })
	void tesSinglePathConversion(final String repo, //
			final String digestType, //
			final String digest) {
		final String expected = "/v2/" + repo + "/blobs/" + digestType + ":" + digest;

		assertEquals(expected, converter.convert(BlobKey.of(repo, digestType, digest)));
	}

}
