package dev.pinchflat.anemoi.registry.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dev.pinchflat.anemoi.registry.controller.resolver.GetManifestRequestArgumentResolver;
import dev.pinchflat.anemoi.registry.service.ManifestService;
import dev.pinchflat.anemoi.registry.service.data.Manifest;
import dev.pinchflat.anemoi.registry.service.data.ManifestKey;

@WebMvcTest(ManifestController.class)
@ContextConfiguration(classes = { ManifestController.class })
public class ManifestControllerIT {

	private static final String NAME = "NAMESPACE";
	private static final String REFERENCE = "REFERENCE";
	private static final byte[] MANIFEST_DATA = "basdasdaeddce".getBytes();
	private MockMvc mockMvc;

	@Autowired
	private ManifestController manifestController;

	@MockBean
	private ManifestService manifestService;

	private File file;
	
	private Manifest manifest;
	
	@BeforeEach
	void setup() throws IOException {
		mockMvc = MockMvcBuilders.standaloneSetup(manifestController).//
				setCustomArgumentResolvers(new GetManifestRequestArgumentResolver()).//
				build();
		file = File.createTempFile(NAME, REFERENCE);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(MANIFEST_DATA);
			fos.flush();
		}
		final Path path = mock(Path.class);

		manifest = new Manifest(ManifestKey.of(NAME, REFERENCE), path, MediaType.APPLICATION_JSON_VALUE);
		
		when(manifestService.get(manifest.getKey())).thenReturn(manifest);
		when(path.toFile()).thenReturn(file);
		
	}

	@AfterEach
	void tearDown() {
		file.delete();
		verifyNoMoreInteractions(manifestService);
	}

	@ParameterizedTest
	@EnumSource(value = HttpMethod.class, names = { "HEAD", "GET" })
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnValidResponseForExistingManifest(HttpMethod method) throws Exception {
		
		this.mockMvc. //
				perform(request(method, "/v2/" + NAME + "/manifests/" + REFERENCE)).//
				andExpect(status().isOk()). //
				andExpect(header().string("Content-Type", manifest.getMediaType())). //
				andExpect(content()
						.bytes(HttpMethod.HEAD.equals(method) ? new byte[0] : MANIFEST_DATA));
		verify(manifestService, times(1)).get(eq(manifest.getKey()));
	}
}
