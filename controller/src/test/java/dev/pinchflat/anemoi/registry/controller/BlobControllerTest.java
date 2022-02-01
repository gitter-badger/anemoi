package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.Blob;
import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.UploadSession;
import dev.pinchflat.anemoi.registry.controller.response.BlobMountedResponse;
import dev.pinchflat.anemoi.registry.controller.response.BlobUploadedResponse;
import dev.pinchflat.anemoi.registry.controller.response.GetBlobResponse;
import dev.pinchflat.anemoi.registry.controller.response.RegistryErrorResponse;
import dev.pinchflat.anemoi.registry.controller.response.UploadSessionResponse;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.service.BlobService;

class BlobControllerTest {
	private static final String REPOSITORY = "REPOSITORY";
	private static final String REFERENCE = "sha256:abcdef01234566789";

	private final BlobService blobService;
	private final BlobController blobController;
	private final Id id = new Id(REPOSITORY, REFERENCE);

	BlobControllerTest() {
		blobService = mock(BlobService.class);
		blobController = new BlobController(blobService);
	}

	@BeforeEach
	void setup() throws IOException {
		reset(blobService);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(blobService);
	}

	@Test
	void testGetNormalBlob() {
		final Path path = Path.of("test");
		final Blob blob = new Blob(id, path, 100);

		when(blobService.get(id)).thenReturn(blob);

		final GetBlobResponse actual = blobController.getBlob(id);

		assertNotNull(actual);
		assertEquals(100, actual.length());
		assertFalse(actual.mounted());
		assertEquals(path, actual.path());
		assertEquals(REFERENCE, actual.reference());
		assertEquals(REPOSITORY, actual.repository());

		verify(blobService, times(1)).get(id);
	}

	@Test
	void testGetMountedBlob() {
		final Path path = Path.of("test");
		final Blob blob = new Blob(new Id("INVALID", "INVALIDREF"), new Blob(id, path, 100));

		when(blobService.get(id)).thenReturn(blob);

		final GetBlobResponse actual = blobController.getBlob(id);

		assertNotNull(actual);
		assertEquals(0, actual.length());
		assertTrue(actual.mounted());
		assertNull(actual.path());
		assertEquals(REFERENCE, actual.reference());
		assertEquals(REPOSITORY, actual.repository());

		verify(blobService, times(1)).get(id);
	}

	@Test
	void testDeleteBLob() {
		assertDoesNotThrow(() -> blobController.deleteBlob(id));
		verify(blobService, times(1)).delete(id);
	}

	@Test
	void testStartUploadSession() {
		final UploadSession bus = new UploadSession(id, 1, 100);

		when(blobService.startUploadSession(REPOSITORY)).thenReturn(bus);

		final UploadSessionResponse actual = blobController.startUploadSession(REPOSITORY);

		assertNotNull(actual);
		assertEquals("1-100", actual.range());
		assertEquals(REPOSITORY, actual.repository());
		assertEquals(REFERENCE, actual.reference());
		verify(blobService, times(1)).startUploadSession(REPOSITORY);
	}

	@Test
	void testMountBlob() {
		final Blob source = new Blob(id, null);
		final Blob blob = new Blob(new Id("TARGET", REFERENCE), source);

		when(blobService.mount("TARGET", id)).thenReturn(blob);

		final BlobMountedResponse actual = blobController.mountBlob("TARGET", REFERENCE, REPOSITORY);

		assertNotNull(actual);
		assertEquals("TARGET", actual.repository());
		assertEquals(REFERENCE, actual.reference());
		
		verify(blobService, times(1)).mount("TARGET", id);
	}

	@Test
	void testInitiateMonolithicBlobUpload() {

		@SuppressWarnings("deprecation")
		final RegistryErrorResponse actual = blobController.initiateMonolithicBlobUpload(REPOSITORY);

		assertNotNull(actual);
		assertNotNull(actual.error());
		assertEquals(RegistryErrorType.UNSUPPORTED, actual.error().type());
	}

	@Test
	void testStreamUpload() {
		final Path path = Path.of("test");
		final Chunk chunk = new Chunk(path, 0, 3, 4);

		final UploadSession bus = new UploadSession(id, 0, 100);

		when(blobService.writeChunk(id, chunk)).thenReturn(bus);

		final UploadSessionResponse actual = blobController.streamUpload(id, chunk);

		assertNotNull(actual);
		assertEquals("0-100", actual.range());
		assertEquals(REFERENCE, actual.reference());
		assertEquals(REPOSITORY, actual.repository());

		verify(blobService, times(1)).writeChunk(id, chunk);
	}

	@Test
	void testBlobUpload() {
		final Path path = Path.of("test");
		final Chunk chunk = new Chunk(path, 0, 3, 4);

		final Path blobPath = Path.of("blob");
		final Blob blob = new Blob(id, blobPath, 100);

		when(blobService.endUploadSession(id, chunk, REFERENCE)).thenReturn(blob);

		final BlobUploadedResponse actual = blobController.blobUpload(id, chunk, REFERENCE);

		assertNotNull(actual);

		assertEquals("0-99", actual.range());
		assertEquals(REFERENCE, actual.reference());
		assertEquals(REPOSITORY, actual.repository());

		verify(blobService, times(1)).endUploadSession(id, chunk, REFERENCE);
	}

}
