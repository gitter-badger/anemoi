package dev.pinchflat.anemoi.registry.controller.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.controller.response.RegistryErrorResponse;
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
		RegistryErrorResponse actual = advice.handle(irne);
		
		assertNotNull(actual);
		assertNotNull(actual.error());
		assertEquals(actual.error().type(), RegistryErrorType.NAME_INVALID);
		assertEquals("test", actual.error().detail());
	}

	@Test
	void testHandleInvalidDigestException() {
		InvalidDigestException ex = new InvalidDigestException("test");
		RegistryErrorResponse actual = advice.handle(ex);
		
		assertNotNull(actual);
		assertNotNull(actual.error());
		assertEquals(actual.error().type(), RegistryErrorType.DIGEST_INVALID);
		assertEquals("test", actual.error().detail());
	}

	@Test
	void testHandleInvalidTagException() {
		InvalidTagException ex = new InvalidTagException("test");
		RegistryErrorResponse actual = advice.handle(ex);
		
		assertNotNull(actual);
		assertNotNull(actual.error());
		assertEquals(actual.error().type(), RegistryErrorType.TAG_INVALID);
		assertEquals("test", actual.error().detail());
	}

	@Test
	void testHandleInvalidBlobUploadException() {
		
		InvalidBlobUploadException ex = new InvalidBlobUploadException("test");
		RegistryErrorResponse actual = advice.handle(ex);
		
		assertNotNull(actual);
		assertNotNull(actual.error());
		assertEquals(actual.error().type(), RegistryErrorType.BLOB_UPLOAD_INVALID);
		assertEquals("test", actual.error().detail());
	}

}
