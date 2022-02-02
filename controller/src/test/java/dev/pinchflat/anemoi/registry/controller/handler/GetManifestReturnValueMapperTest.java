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

import dev.pinchflat.anemoi.registry.controller.response.GetManifestResponse;

class GetManifestReturnValueMapperTest {
	
	private final GetManifestReturnValueMapper mapper = new GetManifestReturnValueMapper();
	
	@Test
	void testGetStatus() {
		assertEquals(HttpStatus.OK, mapper.getStatus(null));
	}

	@Test
	void testGetHeaders() {
		Map<String,String> expected = Map.of("Content-Type","content/type");
		
		GetManifestResponse resp = new GetManifestResponse("content/type",Path.of("test"));
		Map<String,String> actual= mapper.getHeaders("/path", resp);
		
		assertEquals(expected.size(),actual.size());
		expected.entrySet().forEach(kv->assertEquals(kv.getValue(), actual.get(kv.getKey())));

	}

	@Test
	void testGetResourceWithGetMethod() {
		GetManifestResponse resp = new GetManifestResponse("content/type",Path.of("test"));
		Path actual = mapper.getResource("GET", resp);
		
		assertEquals(resp.path(),actual);
	}
	
	@Test
	void testGetResourceWithHeadMethod() {
		GetManifestResponse resp = new GetManifestResponse("content/type",Path.of("test"));
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
			      Arguments.of(GetManifestResponse.class, true),
			      Arguments.of(String.class, false)
			    );
	}

}
