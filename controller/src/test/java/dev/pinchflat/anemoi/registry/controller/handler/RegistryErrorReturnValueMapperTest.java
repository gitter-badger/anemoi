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

import dev.pinchflat.anemoi.registry.controller.response.RegistryErrorResponse;
import dev.pinchflat.anemoi.registry.error.RegistryError;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;

class RegistryErrorReturnValueMapperTest {

	private final RegistryErrorReturnValueMapper mapper = new RegistryErrorReturnValueMapper();
	
	@Test
	void testGetStatus() {
		RegistryErrorResponse re = new RegistryErrorResponse(new RegistryError(RegistryErrorType.DENIED));
		HttpStatus actual = mapper.getStatus(re);
		assertEquals(HttpStatus.valueOf(RegistryErrorType.DENIED.getStatus()), actual);
	}

	@Test
	void testGetResponseObject() {
		RegistryErrorResponse re = new RegistryErrorResponse(new RegistryError(RegistryErrorType.DENIED,"test"));
		ErrorResponse actual = mapper.getResponseObject("GET",re);
		assertEquals(1, actual.errors().size());
		assertEquals(RegistryErrorType.DENIED.toString(), actual.errors().get(0).code());
		assertEquals(RegistryErrorType.DENIED.getMessage(), actual.errors().get(0).message());
		assertEquals("test", actual.errors().get(0).detail());
		
	}
	
	@Test
	void testGetHeaders() {
		assertEquals(Map.of(), mapper.getHeaders(null, null));
	}

	@Test
	void testGetResource() {
		assertNull(mapper.getResource(null, null));
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
			      Arguments.of(RegistryErrorResponse.class, true),
			      Arguments.of(String.class, false)
			    );
	}

}
