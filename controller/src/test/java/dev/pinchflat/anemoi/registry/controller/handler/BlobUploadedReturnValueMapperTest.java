package dev.pinchflat.anemoi.registry.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;

import dev.pinchflat.anemoi.registry.controller.response.BlobUploadedResponse;

class BlobUploadedReturnValueMapperTest {
	private static final String REPOSITORY = "REPO";
	private static final String REFERENCE = "REF";
	private static final String RANGE = "0-99";
	private static final String LOCATION = "/v2/"+REPOSITORY+"/blobs/"+REFERENCE;

	private final BlobUploadedReturnValueMapper mapper = new BlobUploadedReturnValueMapper();

	@Test
	void testGetStatus() {
		assertEquals(HttpStatus.NO_CONTENT, mapper.getStatus(null));
	}

	@Test
	void testGetHeaders() {
		BlobUploadedResponse bur = new BlobUploadedResponse(REPOSITORY, REFERENCE, RANGE);
		final Map<String, String> expected = Map.of("Range", RANGE, "Content-Length", "0", "Location", LOCATION);

		final Map<String, String> actual = mapper.getHeaders("/test", bur);
		assertEquals(expected, actual);
	}

	@Test
	void testGetResource() {
		assertNull(mapper.getResource(null, null));
	}

	@Test
	void testGetResponseObject() {
		assertNull(mapper.getResponseObject(null, null));
	}

	@ParameterizedTest
	@MethodSource("supportMethodSource")
	void testSupportsReturnType(Class<?> supportedClazz,boolean expected) {
		MethodParameter p = mock(MethodParameter.class);

		Mockito.when(p.getParameterType()).then(new Answer<Class<?>>() {
			@Override
			public Class<?> answer(InvocationOnMock invocation) throws Throwable {
				return supportedClazz;
			}
		});
		
		assertEquals(expected, mapper.supportsReturnType(p));

		Mockito.verify(p, Mockito.times(1)).getParameterType();
		Mockito.verifyNoMoreInteractions(p);
	}
	
	
	private static Stream<Arguments> supportMethodSource() {
		 return Stream.of(
			      Arguments.of(BlobUploadedResponse.class, true),
			      Arguments.of(String.class, false)
			    );
	}
}
