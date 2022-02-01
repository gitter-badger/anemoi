package dev.pinchflat.anemoi.registry.controller.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.Chunk;

class ChunkMethodArgumentResolverTest {
	
	private final ChunkMethodArgumentResolver resolver = new ChunkMethodArgumentResolver();
	
	@Test
	void testIsPathParser() {
		assertFalse(resolver.isPathParser());
	}
	
	@Test
	void testIsBodyParser() {
		assertTrue(resolver.isBodyParser());
	}
	
	@Test
	void testParseWithoutHeaders() {
		final Path path = Path.of("test");
		long size = 100;
		
		Chunk chunk = resolver.parse(path, size, null, null);
		
		assertEquals(path, chunk.path());
		assertEquals(100, chunk.size());
		assertEquals(0, chunk.offsetStart());
		assertEquals(0, chunk.offsetEnd());
	}
	
	@Test
	void testParseWithoutContentLength() {
		final Path path = Path.of("test");
		long size = 100;
		
		Chunk chunk = resolver.parse(path, size, "1-1000", null);
		
		assertEquals(path, chunk.path());
		assertEquals(100, chunk.size());
		assertEquals(0, chunk.offsetStart());
		assertEquals(0, chunk.offsetEnd());
	}
	
	@Test
	void testParseWithoutContentRange() {
		final Path path = Path.of("test");
		long size = 100;
		
		Chunk chunk = resolver.parse(path, size, null, "1000");
		
		assertEquals(path, chunk.path());
		assertEquals(100, chunk.size());
		assertEquals(0, chunk.offsetStart());
		assertEquals(0, chunk.offsetEnd());
	}
	
	@Test
	void testParseWitnInvalidContentRange() {
		final Path path = Path.of("test");
		long size = 100;
		
		Chunk chunk = resolver.parse(path, size, "2-", "1000");
		
		assertEquals(path, chunk.path());
		assertEquals(100, chunk.size());
		assertEquals(0, chunk.offsetStart());
		assertEquals(0, chunk.offsetEnd());
	}
	
	@Test
	void testParse() {
		final Path path = Path.of("test");
		long size = 100;
		
		Chunk chunk = resolver.parse(path, size, "2-1001", "1000");
		
		assertEquals(path, chunk.path());
		assertEquals(1000, chunk.size());
		assertEquals(2, chunk.offsetStart());
		assertEquals(1001, chunk.offsetEnd());
	}
}
