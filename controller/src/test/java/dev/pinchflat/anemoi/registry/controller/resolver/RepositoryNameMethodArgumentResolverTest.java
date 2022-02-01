package dev.pinchflat.anemoi.registry.controller.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RepositoryNameMethodArgumentResolverTest {
	private final static String REPOSITORY = "REPO";
	private final RepositoryNameMethodArgumentResolver resolver = new RepositoryNameMethodArgumentResolver();
	
	@Test
	void testIsPathParser() {
		assertTrue(resolver.isPathParser());
	}
	
	@Test
	void testIsBodyParser() {
		assertFalse(resolver.isBodyParser());
	}
	
	@Test
	void testParseInvalidPath() {
		assertThrows(IllegalArgumentException.class, () -> resolver.parse("/"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"/v2/"+REPOSITORY+"/blobs/uploads"			
	})
	void testParseUploadPath(String path) {
		final String actual = resolver.parse(path);
		assertEquals(REPOSITORY, actual );
	}

}
