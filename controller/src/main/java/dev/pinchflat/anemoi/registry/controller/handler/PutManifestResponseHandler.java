package dev.pinchflat.anemoi.registry.controller.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.controller.response.PutManifestResponse;

@Component
public class PutManifestResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return PutManifestResponse.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final PutManifestResponse manifestResponse = (PutManifestResponse) returnValue;
		if (manifestResponse != null) {
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();
			
			response.setStatus(HttpStatus.CREATED.value());
			response.setContentLength(0);
			response.setHeader("Location", "/v2/"+manifestResponse.repository()+"/manifests/"+manifestResponse.reference());
			response.setHeader("Docker-Content-Digest", manifestResponse.digest());
		}

	}

}
