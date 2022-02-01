package dev.pinchflat.anemoi.registry.controller.handler;

import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.controller.response.GetBlobResponse;

@Component
public class GetBlobResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return GetBlobResponse.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final GetBlobResponse resp = (GetBlobResponse) returnValue;
		if (resp != null) {
			final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

			if (resp.mounted()) {
				response.setHeader("Location", "/v2/" + resp.repository() + "/blobs/" + resp.reference());
			} else {
				response.setStatus(HttpStatus.OK.value());
				response.setContentLengthLong(resp.length());
				response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				if ("GET".equals(request.getMethod())) {
					try (FileInputStream fis = new FileInputStream(resp.path().toFile())) {
						IOUtils.copy(fis, response.getOutputStream());
					}
				}
			}
		}
	}

}
