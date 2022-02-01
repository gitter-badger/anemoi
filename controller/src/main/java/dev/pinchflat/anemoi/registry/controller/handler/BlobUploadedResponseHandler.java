package dev.pinchflat.anemoi.registry.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.controller.response.BlobUploadedResponse;

@Component
public class BlobUploadedResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return BlobUploadedResponse.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final BlobUploadedResponse resp = (BlobUploadedResponse) returnValue;
		if (resp != null) {
			final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

			response.setStatus(HttpStatus.NO_CONTENT.value());
			response.addHeader("Range", resp.range());
			response.addHeader("Content-Length", "0");
			response.addHeader("Location", "/v2/"+resp.repository()+"/blobs/"+resp.reference());
		}
	}
}
