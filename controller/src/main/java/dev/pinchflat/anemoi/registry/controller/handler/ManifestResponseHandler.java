package dev.pinchflat.anemoi.registry.controller.handler;

import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.Manifest;

@Component
public class ManifestResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return Manifest.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final Manifest manifest = (Manifest) returnValue;
		if (manifest != null) {
			final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();
			if ("PUT".equals(request.getMethod())) {
				response.setStatus(HttpStatus.CREATED.value());
				response.setContentLength(0);
				response.setHeader("Location", "/v2/"+manifest.id().repository()+"/manifests/"+manifest.id().reference());
				response.setHeader("Docker-Content-Digest", manifest.digest());
			} else {
				response.setStatus(HttpStatus.OK.value());
				response.setContentType(manifest.contentType());
				if ("GET".equals(request.getMethod())) {
					try (FileInputStream fis = new FileInputStream(manifest.path().toFile())) {
						IOUtils.copy(fis, response.getOutputStream());
					}
				}
			}
		}

	}

}
