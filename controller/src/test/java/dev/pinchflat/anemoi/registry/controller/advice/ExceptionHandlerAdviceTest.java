package dev.pinchflat.anemoi.registry.controller.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.error.RegistryError;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.exception.InvalidBlobUploadException;
import dev.pinchflat.anemoi.registry.exception.InvalidDigestException;
import dev.pinchflat.anemoi.registry.exception.InvalidRepositoryNameException;
import dev.pinchflat.anemoi.registry.exception.InvalidTagException;

class ExceptionHandlerAdviceTest {
	
	private final ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
	
	@Test
	void testHandleInvalidRepositoryNameException() {
		InvalidRepositoryNameException irne = new InvalidRepositoryNameException("test");
		RegistryError actual = advice.handle(irne);
		
		assertNotNull(actual);
		assertEquals(actual.type(), RegistryErrorType.NAME_INVALID);
		assertEquals("test", actual.detail());
	}

	@Test
	void testHandleInvalidDigestException() {
		InvalidDigestException ex = new InvalidDigestException("test");
		RegistryError actual = advice.handle(ex);
		
		assertNotNull(actual);
		assertEquals(actual.type(), RegistryErrorType.DIGEST_INVALID);
		assertEquals("test", actual.detail());
	}

	@Test
	void testHandleInvalidTagException() {
		InvalidTagException ex = new InvalidTagException("test");
		RegistryError actual = advice.handle(ex);
		
		assertNotNull(actual);
		assertEquals(actual.type(), RegistryErrorType.TAG_INVALID);
		assertEquals("test", actual.detail());
	}

	@Test
	void testHandleInvalidBlobUploadException() {
		InvalidBlobUploadException ex = new InvalidBlobUploadException("test");
		RegistryError actual = advice.handle(ex);
		
		assertNotNull(actual);
		assertEquals(actual.type(), RegistryErrorType.BLOB_UPLOAD_INVALID);
		assertEquals("test", actual.detail());
	}

}
