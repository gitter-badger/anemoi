package dev.pinchflat.anemoi.registry.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
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

import dev.pinchflat.anemoi.registry.controller.response.GetBlobResponse;

class GetBlobReturnValueMapperTest {
	
private final GetBlobReturnValueMapper mapper = new GetBlobReturnValueMapper();
	private static final String REPOSITORY = "repository";
	private static final String REFERENCE = "reference";
	private static final String LOCATION = "/v2/"+REPOSITORY+"/blobs/"+REFERENCE;
	
	@Test
	void testGetStatus() {
		GetBlobResponse resp = new GetBlobResponse(REPOSITORY, REFERENCE, 0, Path.of("test"), false);
		assertEquals(HttpStatus.OK, mapper.getStatus(resp));
	}
	
	@Test
	void testGetStatusForMounted() {
		GetBlobResponse resp = new GetBlobResponse(REPOSITORY, REFERENCE, 0, Path.of("test"), true);
		assertEquals(HttpStatus.TEMPORARY_REDIRECT, mapper.getStatus(resp));
	}

	@Test
	void testGetHeaders() {
		GetBlobResponse resp = new GetBlobResponse(REPOSITORY, REFERENCE, 0, Path.of("test"), false);
		Map<String,String> expected = Map.of("Content-Length","0", "Content-Type","application/octet-stream");
		Map<String,String> actual = mapper.getHeaders("/path", resp);
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testGetHeadersForMounted() {
		GetBlobResponse resp = new GetBlobResponse(REPOSITORY, REFERENCE, 0, Path.of("test"), true);
		Map<String,String> expected = Map.of("Location",LOCATION);
		Map<String,String> actual = mapper.getHeaders("/path", resp);
		
		assertEquals(expected, actual);
	}

	@Test
	void testGetResourceWithGetMethod() {
		GetBlobResponse resp = new GetBlobResponse(REPOSITORY, REFERENCE, 0, Path.of("test"), true);
		Path actual = mapper.getResource("GET", resp);
		
		assertEquals(resp.path(),actual);
	}
	
	@Test
	void testGetResourceWithHeadMethod() {
		GetBlobResponse resp = new GetBlobResponse(REPOSITORY, REFERENCE, 0, Path.of("test"), true);
		Path actual = mapper.getResource("HEAD", resp);
		
		assertNull(actual);
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
			      Arguments.of(GetBlobResponse.class, true),
			      Arguments.of(String.class, false)
			    );
	}

}
