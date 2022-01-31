package dev.pinchflat.anemoi.registry.controller.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.UploadSession;

@Component
public class UploadSessionResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return UploadSession.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final UploadSession session = (UploadSession) returnValue;
		if (session != null) {
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

			response.setStatus(HttpStatus.ACCEPTED.value());
			response.addHeader("Content-Length", "0");
			response.addHeader("Range", session.getRange());
			response.addHeader("Blob-Upload-Session-ID", session.id().reference());
			response.addHeader("Location", "/v2/"+session.id().repository()+"/blobs/uploads/"+session.id().reference());
		}

	}

}
