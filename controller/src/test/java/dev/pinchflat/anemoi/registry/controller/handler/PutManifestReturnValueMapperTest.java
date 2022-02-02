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

import dev.pinchflat.anemoi.registry.controller.response.PutManifestResponse;

class PutManifestReturnValueMapperTest {
	private static final String REPOSITORY = "REPO";
	private static final String REFERENCE = "REF";
	private static final String DIGEST= "DIGEST";
	private static final String LOCATION = "/v2/"+REPOSITORY+"/manifests/"+REFERENCE;
	
	private final PutManifestReturnValueMapper mapper = new PutManifestReturnValueMapper();
	
	@Test
	void testGetStatus() {
		assertEquals(HttpStatus.CREATED, mapper.getStatus(null));
	}

	@Test
	void testGetHeaders() {
		Map<String,String> expected = Map.of("Content-Length","0","Location",LOCATION,"Docker-Content-Digest",DIGEST);
		PutManifestResponse response = new PutManifestResponse(REPOSITORY,REFERENCE,DIGEST);
		Map<String,String> actual = mapper.getHeaders("PUT", response);
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
			      Arguments.of(PutManifestResponse.class, true),
			      Arguments.of(String.class, false)
			    );
	}
}
