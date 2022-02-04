package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dev.pinchflat.anemoi.registry.Chunk;
import dev.pinchflat.anemoi.registry.Id;
import dev.pinchflat.anemoi.registry.Manifest;
import dev.pinchflat.anemoi.registry.controller.config.RegistryWebMvcConfigurer;
import dev.pinchflat.anemoi.registry.controller.resolver.ChunkMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.controller.resolver.IdMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryNameMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.service.ManifestService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ManifestController.class)
@ContextConfiguration(classes = { //
		RegistryWebMvcConfigurer.class, //
		ManifestController.class, //
		IdMethodArgumentResolver.class, //
		ChunkMethodArgumentResolver.class,//
		RepositoryNameMethodArgumentResolver.class})
class ManifestControllerTest {
	private static final String REPOSITORY = "REPOSITORY";
	private static final String REPOSITORY_WITH_NS = "NS/REPOSITORY";
	private static final String REPOSITORY_WITH_MULTI_NS = "NS/NS2/REPOSITORY";
	private static final String REFERENCE = "REFERENCE";
	private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8_VALUE;
	private static final String CONTENT = "{'a':'b'}";
	private static final long CONTENT_LENGTH = CONTENT.getBytes().length;
	private static final String DIGEST = "digest";

	@MockBean
	ManifestService manifestService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		reset(manifestService);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(manifestService);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testGetManifest(String repositoryName) throws Exception {
		var file = File.createTempFile("test", "manifest");
		file.deleteOnExit();
		var path = file.toPath();
		Files.writeString(path, CONTENT);
		Id id = new Id(repositoryName, REFERENCE);
		Manifest manifest = new Manifest(id, path, CONTENT_TYPE, CONTENT_LENGTH, DIGEST);

		when(manifestService.get(id)).thenReturn(manifest);

		mockMvc//
				.perform(get("/v2/" + repositoryName + "/manifests/" + REFERENCE))//
				.andExpect(MockMvcResultMatchers.status().isOk())//
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", CONTENT_TYPE))//
				.andExpect(MockMvcResultMatchers.header().string("Docker-Content-Digest", DIGEST))//
				.andExpect(MockMvcResultMatchers.content().json(CONTENT));

		verify(manifestService, times(1)).get(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testGetManifestWithHead(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, REFERENCE);
		Manifest manifest = new Manifest(id, null, CONTENT_TYPE, CONTENT_LENGTH, DIGEST);

		when(manifestService.get(id)).thenReturn(manifest);

		mockMvc//
				.perform(head("/v2/" + repositoryName + "/manifests/" + REFERENCE))//
				.andExpect(MockMvcResultMatchers.status().isOk())//
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", CONTENT_TYPE))//
				.andExpect(MockMvcResultMatchers.header().string("Docker-Content-Digest", DIGEST))//
				.andExpect(MockMvcResultMatchers.content().bytes(new byte[0]));

		verify(manifestService, times(1)).get(id);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testPutManifest(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, REFERENCE);
		Manifest manifest = new Manifest(id, null, CONTENT_TYPE, CONTENT_LENGTH, DIGEST);

		when(manifestService.write(any(String.class), eq(id), any(Chunk.class))).thenReturn(manifest);

		mockMvc//
				.perform(put("/v2/" + repositoryName + "/manifests/" + REFERENCE)//
						.contentType(CONTENT_TYPE)//
						.content(CONTENT))//
				.andExpect(MockMvcResultMatchers.status().isCreated())//
				.andExpect(MockMvcResultMatchers.header().string("Content-Length", "0"))//
				.andExpect(MockMvcResultMatchers.header().string("Docker-Content-Digest", DIGEST))//
				.andExpect(MockMvcResultMatchers.header().string("Location",
						"/v2/" + repositoryName + "/manifests/" + REFERENCE));

		ArgumentCaptor<Chunk> chunk = ArgumentCaptor.forClass(Chunk.class);
		verify(manifestService, times(1)).write(eq(CONTENT_TYPE), eq(id), chunk.capture());
		String actualContent = Files.readString(chunk.getValue().path());
		assertEquals(0, chunk.getValue().offsetEnd());
		assertEquals(0, chunk.getValue().offsetStart());
		assertEquals(CONTENT_LENGTH, chunk.getValue().size());
		assertEquals(CONTENT, actualContent);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS, REPOSITORY_WITH_MULTI_NS })
	void testDeleteManifest(String repositoryName) throws Exception {

		Id id = new Id(repositoryName, REFERENCE);

		doNothing().when(manifestService).delete(id);

		mockMvc//
				.perform(delete("/v2/" + repositoryName + "/manifests/" + REFERENCE)//
						.contentType(CONTENT_TYPE)//
						.content(CONTENT))//
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		verify(manifestService, times(1)).delete(id);
	}

}
