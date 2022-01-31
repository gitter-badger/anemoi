package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.Manifest;
import dev.pinchflat.anemoi.registry.service.ManifestService;

class ManifestControllerTest {

	private static final String REPOSITORY = "REPOSITORY";
	private static final String REFERENCE = "REFERENCE";
	private static final String CONTENT_TYPE = "content/type";
	private static final String DIGEST = "digest";

	private final ManifestService manifestService;
	private final ManifestController manifestController;
	private final Id id;

	ManifestControllerTest() {
		manifestService = mock(ManifestService.class);
		manifestController = new ManifestController(manifestService);
		id = new Id(REPOSITORY, REFERENCE);
	}

	@BeforeEach
	void setup() throws IOException {
		Mockito.reset(manifestService);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(manifestService);
	}

	@Test
	void testGetManifest() {
		final Path path = Path.of("test");
		final Manifest manifest = new Manifest(id, path, CONTENT_TYPE, DIGEST);

		when(manifestService.get(id)).thenReturn(manifest);

		Manifest actual = manifestController.getManifest(id);

		assertNotNull(actual);
		assertEquals(actual.id(), id);
		assertEquals(actual.path(), path);
		assertEquals(actual.contentType(), CONTENT_TYPE);
		assertEquals(actual.digest(), DIGEST);

		verify(manifestService, times(1)).get(eq(id));

	}

	@Test
	void testPutManifest() {
		final Path path = Path.of("test");
		final Chunk chunk = new Chunk(path, 0, 3, 4);
		final Manifest manifest = new Manifest(id, path, CONTENT_TYPE, "digest");

		when(manifestService.write(CONTENT_TYPE, id, chunk)).thenReturn(manifest);

		Manifest actual = manifestController.putManifest(CONTENT_TYPE, id, chunk);

		assertNotNull(actual);
		assertEquals(actual.path(), path);
		assertEquals(actual.contentType(), CONTENT_TYPE);
		assertEquals(actual.digest(), DIGEST);

		verify(manifestService, times(1)).write(CONTENT_TYPE, id, chunk);
	}

	@Test
	void testDeleteManifest() {
		assertDoesNotThrow(() -> manifestController.deleteManifest(id));
		verify(manifestService, times(1)).delete(id);
	}
}
