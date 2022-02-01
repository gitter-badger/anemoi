package dev.pinchflat.anemoi.registry.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pinchflat.anemoi.registry.controller.response.TagsResponse;

@Component
public class TagsResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return TagsResponseHandler.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final TagsResponse tagsResponse= (TagsResponse) returnValue;
		if (tagsResponse != null) {
			final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

			response.setStatus(HttpStatus.OK.value());
			response.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			if (tagsResponse.hasNext()) {
				response.addHeader("Link", request.getRequestURI()+"?n="+tagsResponse.count()+"&last="+tagsResponse.nextPageOffset()+"; rel=\"next\"");
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getWriter(), tagsResponse.tags());
		}
	}
}
