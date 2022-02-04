package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import dev.pinchflat.anemoi.registry.Blob;
import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.UploadSession;
import dev.pinchflat.anemoi.registry.controller.config.RegistryWebMvcConfigurer;
import dev.pinchflat.anemoi.registry.controller.resolver.ChunkMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.controller.resolver.IdMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryNameMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.error.RegistryException;
import dev.pinchflat.anemoi.registry.service.BlobService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BlobController.class)
@TestPropertySource(locations = "classpath:unsecured.properties")
@ContextConfiguration(classes = { //
		RegistryWebMvcConfigurer.class, //
		BlobController.class, //
		IdMethodArgumentResolver.class, //
		ChunkMethodArgumentResolver.class, //
		RepositoryNameMethodArgumentResolver.class })
@AutoConfigureMockMvc(addFilters = false)
public class BlobControllerTest {

	private static final String REPOSITORY = "REPOSITORY";
	private static final String REPOSITORY_WITH_NS = "NS/REPOSITORY";
	private static final String REPOSITORY_WITH_MULTI_NS = "NS/NS2/REPOSITORY";
	private static final String REFERENCE = "sha256:abcdef0123456789";
	private static final String CONTENT = "{'a':'b'}";
	private static final long CONTENT_LENGTH = CONTENT.getBytes().length;
	private static final String DIGEST = "digest";

	@MockBean
	private BlobService blobService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		reset(blobService);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(blobService);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testGetBlob(String repositoryName) throws Exception {
		var file = File.createTempFile("test", "blob");
		file.deleteOnExit();
		var path = file.toPath();
		Files.writeString(path, CONTENT);
		Id id = new Id(repositoryName, REFERENCE);
		Blob blob = new Blob(id, path, CONTENT_LENGTH);

		when(blobService.get(id)).thenReturn(blob);

		mockMvc//
				.perform(get("/v2/" + repositoryName + "/blobs/" + REFERENCE))//
				.andExpect(status().isOk())//
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE))//
				.andExpect(header().longValue("Content-Length", CONTENT_LENGTH))//
				.andExpect(header().string("Docker-Content-Digest", REFERENCE))//
				.andExpect(content().json(CONTENT));

		verify(blobService, times(1)).get(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testGetBlobtWithHead(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, REFERENCE);
		Blob blob = new Blob(id, null, CONTENT_LENGTH);

		when(blobService.get(id)).thenReturn(blob);

		mockMvc//
				.perform(head("/v2/" + repositoryName + "/blobs/" + REFERENCE))//
				.andExpect(status().isOk())//
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE))//
				.andExpect(header().longValue("Content-Length", CONTENT_LENGTH))//
				.andExpect(header().string("Docker-Content-Digest", REFERENCE))//
				.andExpect(content().bytes(new byte[0]));

		verify(blobService, times(1)).get(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testGetBlobForRedirect(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, "sha256:0123abc");
		Blob blob = new Blob(id, new Blob(new Id(repositoryName, REFERENCE), null, CONTENT_LENGTH));

		when(blobService.get(id)).thenReturn(blob);

		mockMvc//
				.perform(head("/v2/" + repositoryName + "/blobs/sha256:0123abc"))//
				.andExpect(status().isTemporaryRedirect())//
				.andExpect(header().string("Location", "/v2/" + repositoryName + "/blobs/" + REFERENCE))//
				.andExpect(header().string("Docker-Content-Digest", REFERENCE))//
				.andExpect(content().bytes(new byte[0]));

		verify(blobService, times(1)).get(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testDeleteBlob(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, REFERENCE);
		Blob blob = new Blob(id, null, CONTENT_LENGTH);

		when(blobService.delete(id)).thenReturn(blob);

		mockMvc//
				.perform(delete("/v2/" + repositoryName + "/blobs/" + REFERENCE))//
				.andExpect(status().isAccepted())//
				.andExpect(header().longValue("Content-Length", 0))//
				.andExpect(header().string("Docker-Content-Digest", REFERENCE));

		verify(blobService, times(1)).delete(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testMonoliticUpload(String repositoryName) throws Exception {
		NestedServletException nse = assertThrows(NestedServletException.class, () -> mockMvc//
				.perform(post("/v2/" + repositoryName + "/blobs/uploads").queryParam("digest", DIGEST)));
		
		assertInstanceOf(RegistryException.class, nse.getRootCause());
		RegistryException re= (RegistryException) nse.getCause();
		assertEquals(RegistryErrorType.UNSUPPORTED, re.getRegistryErrorType());
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testStartUploadSession(String repositoryName) throws Exception {
		Id id = new Id(repositoryName, REFERENCE);
		UploadSession us = new UploadSession(id, 0, 0);

		when(blobService.startUploadSession(repositoryName)).thenReturn(us);

		mockMvc//
				.perform(post("/v2/" + repositoryName + "/blobs/uploads"))//
				.andExpect(status().isAccepted())//
				.andExpect(header().longValue("Content-Length", 0))//
				.andExpect(header().string("Blob-Upload-Session-ID", REFERENCE))//
				.andExpect(header().string("Location", "/v2/" + repositoryName + "/blobs/uploads/" + REFERENCE));

		verify(blobService, times(1)).startUploadSession(repositoryName);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testMountBlob(String repositoryName) throws Exception {
		Id id = new Id(repositoryName, "sha256:0123456");
		Id sourceId = new Id("SOURCE_REPO", REFERENCE);
		Blob blob = new Blob(id, null, 100);

		when(blobService.mount(repositoryName, sourceId)).thenReturn(blob);

		mockMvc//
				.perform(//
						post("/v2/" + repositoryName + "/blobs/uploads")//
								.queryParam("mount", REFERENCE)//
								.queryParam("from", "SOURCE_REPO"))//
				.andExpect(status().isCreated())//
				.andExpect(header().longValue("Content-Length", 0))//
				.andExpect(header().string("Blob-Upload-Session-ID", "sha256:0123456"))//
				.andExpect(header().string("Location", "/v2/" + repositoryName + "/blobs/sha256:0123456"));

		verify(blobService, times(1)).mount(repositoryName, sourceId);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testGetBlobUpload(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, REFERENCE);
		UploadSession us = new UploadSession(id, 0, CONTENT_LENGTH - 1);
		when(blobService.getUploadStatus(eq(id))).thenReturn(us);

		mockMvc//
				.perform(get("/v2/" + repositoryName + "/blobs/uploads/" + REFERENCE))//
				.andExpect(status().isNoContent())//
				.andExpect(header().longValue("Content-Length", 0))//
				.andExpect(header().string("Range", "0-" + (CONTENT_LENGTH - 1)))//
				.andExpect(header().string("Blob-Upload-Session-ID", REFERENCE));

		verify(blobService, times(1)).getUploadStatus(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testStreamUpload(String repositoryName) throws Exception {
		var file = File.createTempFile("test", "blob");
		file.deleteOnExit();
		var path = file.toPath();
		Files.writeString(path, CONTENT);

		Id id = new Id(repositoryName, REFERENCE);
		UploadSession us = new UploadSession(id, 0, CONTENT_LENGTH - 1);
		when(blobService.writeChunk(eq(id), any(Chunk.class))).thenReturn(us);

		mockMvc//
				.perform(//
						patch("/v2/" + repositoryName + "/blobs/uploads/" + REFERENCE)//
								.content(CONTENT))//
				.andExpect(status().isAccepted())//
				.andExpect(header().longValue("Content-Length", 0))//
				.andExpect(header().string("Range", "0-" + (CONTENT_LENGTH - 1)))//
				.andExpect(header().string("Blob-Upload-Session-ID", REFERENCE))//
				.andExpect(header().string("Location", "/v2/" + repositoryName + "/blobs/uploads/" + REFERENCE));

		ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
		verify(blobService, times(1)).writeChunk(eq(id), captor.capture());

		Chunk actual = captor.getValue();
		String actualContent = Files.readString(actual.path());
		assertEquals(0, actual.offsetEnd());
		assertEquals(0, actual.offsetStart());
		assertEquals(CONTENT_LENGTH, actual.size());
		assertEquals(CONTENT, actualContent);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testBlobUpload(String repositoryName) throws Exception {
		Id uploadId = new Id(repositoryName, "sessionId");
		Id id = new Id(repositoryName, REFERENCE);
		Blob blob = new Blob(id, Path.of("test"), CONTENT_LENGTH);

		when(blobService.endUploadSession(eq(uploadId), any(Chunk.class), eq(DIGEST))).thenReturn(blob);

		mockMvc//
				.perform(//
						put("/v2/" + repositoryName + "/blobs/uploads/sessionId")//
								.queryParam("digest", DIGEST)//
								.content(CONTENT))//
				.andExpect(status().isNoContent())//
				.andExpect(header().longValue("Content-Length", 0))//
				.andExpect(header().string("Location", "/v2/" + repositoryName + "/blobs/" + REFERENCE));

		ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
		verify(blobService, times(1)).endUploadSession(eq(uploadId), captor.capture(), eq(DIGEST));

		Chunk actual = captor.getValue();
		String actualContent = Files.readString(actual.path());
		assertEquals(0, actual.offsetEnd());
		assertEquals(0, actual.offsetStart());
		assertEquals(CONTENT_LENGTH, actual.size());
		assertEquals(CONTENT, actualContent);
	}
}
