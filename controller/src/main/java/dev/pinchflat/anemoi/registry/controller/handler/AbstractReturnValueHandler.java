package dev.pinchflat.anemoi.registry.controller.handler;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract class AbstractReturnValueHandler<T> implements HandlerMethodReturnValueHandler {

	private final Class<T> clazz;

	protected AbstractReturnValueHandler(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return clazz.equals(returnType.getParameterType());
	}

	protected HttpStatus getStatus(T value) {
		return HttpStatus.OK;
	}

	protected Map<String,String> getHeaders(String requestPath, T value) {
		return Map.of();
	}
	
	protected Path getResource(String method, T value) {
		return null;
	}
	
	protected Object getResponseObject(String method, T value) {
		return null;
	}
	
	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

		if (clazz.isInstance(returnType)) {
			final T value = clazz.cast(returnType);
			response.setStatus(getStatus(value).value());
			getHeaders(request.getRequestURI(),value).entrySet().forEach(kv -> response.setHeader(kv.getKey(), kv.getValue()));
			
			Optional<Path> path = Optional.ofNullable(getResource(request.getMethod(), value));
			if (path.isPresent()) {
				try (FileInputStream fis = new FileInputStream(path.get().toFile())) {
					IOUtils.copy(fis, response.getOutputStream());
				}
			}
			
			Optional<Object> object = Optional.ofNullable(getResponseObject(request.getMethod(), value));
			if (object.isPresent()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.writeValue(response.getWriter(), object.get());
			}

			response.flushBuffer();
		}else {
			throw new IllegalArgumentException("returnValue is not supported by mapper");
		}
	}

	

	

}
