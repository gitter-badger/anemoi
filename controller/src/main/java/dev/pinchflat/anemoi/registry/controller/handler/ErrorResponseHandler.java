package dev.pinchflat.anemoi.registry.controller.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pinchflat.anemoi.registry.error.RegistryError;

public class ErrorResponseHandler  implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return RegistryError.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final RegistryError error= (RegistryError) returnValue;
		if (error != null) {
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

			response.setStatus(error.type().getStatus());
			ErrorResponse err = new ErrorResponse(error);
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getWriter(), err);
			response.flushBuffer();
		}
	}
}
