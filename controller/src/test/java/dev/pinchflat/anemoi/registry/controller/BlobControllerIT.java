package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dev.pinchflat.anemoi.registry.controller.converter.BlobKeyToLocationHeaderConverter;
import dev.pinchflat.anemoi.registry.controller.converter.BlobUploadSessionToLocationHeaderConverter;
import dev.pinchflat.anemoi.registry.controller.resolver.BlobUploadRequestArgumentResolver;
import dev.pinchflat.anemoi.registry.controller.resolver.GetBlobRequestArgumentResolver;
import dev.pinchflat.anemoi.registry.controller.resolver.InitiateBlobUploadRequestArgumentResolver;
import dev.pinchflat.anemoi.registry.digest.DigestType;
import dev.pinchflat.anemoi.registry.service.BlobService;
import dev.pinchflat.anemoi.registry.service.data.Blob;
import dev.pinchflat.anemoi.registry.service.data.BlobKey;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSession;
import dev.pinchflat.anemoi.registry.service.data.BlobUploadSessionKey;

@WebMvcTest(BlobController.class)
@ContextConfiguration(classes = { BlobController.class, //
		BlobKeyToLocationHeaderConverter.class, //
		BlobUploadSessionToLocationHeaderConverter.class })
public class BlobControllerIT {

	private static final String NAME = "NAMESPACE";
	private static final DigestType DIGEST_TYPE = DigestType.SHA256;
	private static final String DIGEST = "abcdefg0123456789";
	private static final Blob BLOB = new Blob(BlobKey.of(NAME, DIGEST_TYPE.getPrefix(), DIGEST));
	private static final byte[] BLOB_DATA = "basdasdaeddce".getBytes();
	private MockMvc mockMvc;

	@Autowired
	private BlobController blobController;
	
	@MockBean
	private BlobService blobService;

	private File file;

	@BeforeEach
	void setup() throws IOException {
		mockMvc = MockMvcBuilders.standaloneSetup(blobController).//
				setCustomArgumentResolvers(
						new GetBlobRequestArgumentResolver(),
						new InitiateBlobUploadRequestArgumentResolver(),
						new BlobUploadRequestArgumentResolver()).//
				build();
		file = File.createTempFile(NAME, DIGEST);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(BLOB_DATA);
			fos.flush();
		}
		final Path path = mock(Path.class);

		BLOB.setLength(BLOB_DATA.length);
		BLOB.setPath(path);
		BLOB.setSource(Optional.empty());

		when(blobService.get(BLOB.getKey())).thenReturn(BLOB);
		when(path.toFile()).thenReturn(file);

	}

	@AfterEach
	void tearDown() {
		file.delete();
		verifyNoMoreInteractions(blobService);
	}

	@ParameterizedTest
	@EnumSource(value = HttpMethod.class, names = { "HEAD", "GET" })
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnValidResponseForExistingBlob(HttpMethod method) throws Exception {

		this.mockMvc. //
				perform(request(method, "/v2/" + NAME + "/blobs/" + DIGEST_TYPE.getPrefix() + ":" + DIGEST)).//
				andExpect(status().isOk()). //
				andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)). //
				andExpect(header().longValue("Content-Length", BLOB_DATA.length)). //
				andExpect(content().bytes(HttpMethod.HEAD.equals(method) ? new byte[0] : BLOB_DATA));
		verify(blobService, times(1)).get(eq(BLOB.getKey()));
	}

	@Test
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnRedirectForMountedBlob() throws Exception {
		BLOB.setSource(Optional.of(BlobKey.of("REPO", DigestType.SHA512.getPrefix(), "0ab132")));

		when(blobService.get(BLOB.getKey())).thenReturn(BLOB);
		this.mockMvc. //
				perform(head("/v2/" + NAME + "/blobs/" + DIGEST_TYPE.getPrefix() + ":" + DIGEST)).//
				andExpect(status().isTemporaryRedirect()). //
				andExpect(header().string("Location", "/v2/REPO/blobs/sha512:0ab132"));
		verify(blobService, times(1)).get(eq(BLOB.getKey()));
	}
	
	@Test
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnUploadSessionIdForInitiateUpload() throws Exception {
		BlobUploadSession session = new BlobUploadSession(new BlobUploadSessionKey(NAME,"SESSION_ID"));
		
		when(blobService.startUploadSession(NAME)).thenReturn(session);
		
		this.mockMvc. //
				perform(post("/v2/" + NAME + "/blobs/uploads")).//
				andExpect(status().isAccepted()). //
				andExpect(header().longValue("Content-Length", 0)). //
				andExpect(header().string("Range", "0-0")). //
				andExpect(header().string("Blob-Upload-Session-ID", "SESSION_ID"));
				
		verify(blobService, times(1)).startUploadSession(eq(NAME));
	}
	
	@Test
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnUnsupportedForMonolithicUpload() throws Exception {
		
		this.mockMvc. //
				perform(post("/v2/" + NAME + "/blobs/uploads").queryParam("digest", DIGEST)).//
				andExpect(status().isMethodNotAllowed());
	}
	
	@Test
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnRangeForStreamUpload() throws Exception {
		final String sessionId = "sessionId";
		BlobUploadSessionKey key = new BlobUploadSessionKey(NAME, sessionId);
		BlobUploadSession bus = new BlobUploadSession(key);
		bus.setRangeStart(0);
		bus.setRangeEnd(BLOB_DATA.length-1);
		when(blobService.appendChunkToUpload(eq(key), any(Path.class))).thenReturn(bus);
		
		this.mockMvc. //
				perform(patch( "/v2/" + NAME + "/blobs/uploads/" + sessionId).content(BLOB_DATA)).//
				andExpect(status().isAccepted()). //
				andExpect(header().longValue("Content-Length", 0)). //
				andExpect(header().string("Range", "0-"+(BLOB_DATA.length-1))). //
				andExpect(header().string("Location", "/v2/"+ NAME+"/blobs/uploads/"+sessionId));
		ArgumentCaptor<Path> pathCapturer = ArgumentCaptor.forClass(Path.class);
		verify(blobService,times(1)).appendChunkToUpload(eq(key), pathCapturer.capture());
		
		assertTrue(Arrays.equals(BLOB_DATA, Files.readAllBytes(pathCapturer.getValue())));
	}
	
}


