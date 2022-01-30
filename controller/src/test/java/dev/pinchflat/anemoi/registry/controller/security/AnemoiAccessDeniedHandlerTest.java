package dev.pinchflat.anemoi.registry.controller.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

class AnemoiAccessDeniedHandlerTest {
	
	@NotNull
	private final AnemoiAccessDeniedHandler handler = new AnemoiAccessDeniedHandler();
	
	@NotNull
	private HttpServletRequest request = mock(HttpServletRequest.class);
	
	@NotNull
	private HttpServletResponse response = mock(HttpServletResponse.class);
	
	@NotNull
	private AccessDeniedException ade = mock(AccessDeniedException.class);
	
	@AfterEach
	public void tearDown() {
		verifyNoMoreInteractions(request,response,ade);
	}
	
	@Test
	public void doNotModifyResponseWhenCommited() throws IOException, ServletException {
		when(response.isCommitted()).thenReturn(true);
		handler.handle(request, response, ade);
		verify(response, times(1)).isCommitted();
	}

	@Test
	public void writeAccessDeniedToResponse() throws IOException, ServletException {

		PrintWriter pw = mock(PrintWriter.class);
		
		when(response.isCommitted()).thenReturn(false);
		when(response.getWriter()).thenReturn(pw);

		handler.handle(request, response, ade);

		verify(response, times(1)).isCommitted();
		verify(response, times(1)).setContentType(eq(MediaType.APPLICATION_JSON_VALUE));
		verify(response, times(1)).setStatus(eq(HttpStatus.FORBIDDEN.value()));
		verify(response, times(1)).getWriter();
		
		ArgumentCaptor<String> stringCapturer = ArgumentCaptor.forClass(String.class);
		verify(pw,times(1)).write(stringCapturer.capture());

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jo = objectMapper.readTree(stringCapturer.getValue());
		jo = ((ArrayNode)jo.get("errors")).get(0);
		assertEquals("DENIED", jo.path("code").textValue());
		assertEquals("requested access to the resource is denied", jo.path("message").asText());
	}

}
