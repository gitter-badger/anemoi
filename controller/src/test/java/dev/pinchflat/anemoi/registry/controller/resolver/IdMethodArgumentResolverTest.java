package dev.pinchflat.anemoi.registry.controller.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.MethodParameter;

import dev.pinchflat.anemoi.registry.Id;

class IdMethodArgumentResolverTest {
	private final static String REPOSITORY = "REPO";
	private final static String REFERENCE = "REF";
	
	private final IdMethodArgumentResolver resolver = new IdMethodArgumentResolver();

	@Test
	void testIsPathParser() {
		assertTrue(resolver.isPathParser());
	}

	@Test
	void testIsBodyParser() {
		assertFalse(resolver.isBodyParser());
	}

	@Test
	void testSupportsParameter() {
		MethodParameter p = mock(MethodParameter.class);
		
		Mockito.when(p.getParameterType()).then(new Answer<Class<?>>() {
			@Override
			public Class<?> answer(InvocationOnMock invocation) throws Throwable {
				return Id.class;
			}
		});
		assertTrue(resolver.supportsParameter(p));
		
		Mockito.verify(p,Mockito.times(1)).getParameterType();
		Mockito.verifyNoMoreInteractions(p);
	}

	@Test
	void testSupportsParameterWithInvalidType() {
		MethodParameter p = mock(MethodParameter.class);
		Mockito.when(p.getParameterType()).then(new Answer<Class<?>>() {
			@Override
			public Class<?> answer(InvocationOnMock invocation) throws Throwable {
				return String.class;
			}
		});
		assertFalse(resolver.supportsParameter(p));
	}

	@Test
	void testParseInvalidPath() {
		assertThrows(IllegalArgumentException.class, () -> resolver.parse("/"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"/v2/"+REPOSITORY+"/blobs/uploads/"+REFERENCE,//
			"/v2/"+REPOSITORY+"/blobs/"+REFERENCE,//
			"/v2/"+REPOSITORY+"/manifests/"+REFERENCE			
	})
	void testParseUploadPath(String path) {
		final Id id = resolver.parse(path);
		assertEquals(REPOSITORY, id.repository());
		assertEquals(REFERENCE, id.reference());
	}

}
