package dev.pinchflat.anemoi.registry.controller.advice;

import static dev.pinchflat.anemoi.registry.error.RegistryErrorType.UNSUPPORTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import dev.pinchflat.anemoi.registry.error.RegistryException;

class ExceptionHandlerAdviceTest {

	final ExceptionHandlerAdvice advice =  new ExceptionHandlerAdvice();
	
	@Test
	void testHandle() {	
		ResponseEntity<ErrorResponse>  response = advice.handle(new RegistryException(UNSUPPORTED));
		ErrorResponse errorResponse = response.getBody();
		assertEquals(UNSUPPORTED.getStatus(), response.getStatusCodeValue());
		assertEquals(1, errorResponse.errors().size());
		assertEquals(UNSUPPORTED.toString(), errorResponse.errors().get(0).code());
		assertEquals(UNSUPPORTED.getMessage(), errorResponse.errors().get(0).message());
	}
}
