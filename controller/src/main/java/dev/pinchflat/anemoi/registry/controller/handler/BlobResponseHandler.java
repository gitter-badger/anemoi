package dev.pinchflat.anemoi.registry.controller.handler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

import dev.pinchflat.anemoi.registry.Blob;

@Component
public class BlobResponseHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return Blob.class.equals(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		final Blob blob = (Blob) returnValue;
		if (blob != null) {
			final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
			final HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();

			if ("PUT".equals(request.getMethod())) {
				generatePutRespponse(blob, response);
			} else {
				generateGetResponse(blob, request, response);
			}
		}

	}

	private void generatePutRespponse(final Blob blob, final HttpServletResponse response) {
		response.setStatus(HttpStatus.NO_CONTENT.value());
		response.addHeader("Range", blob.getRange());
		response.addHeader("Content-Length", "0");
		response.addHeader("Location", "/v2/"+blob.id().repository()+"/blobs/"+blob.id().reference());
	}

	private void generateGetResponse(final Blob blob, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, FileNotFoundException {
		if (blob.isMounted()) {
			response.setHeader("Location", "/v2/" + blob.id().repository() + "/blobs/" + blob.id().reference());
		} else {
			response.setStatus(HttpStatus.OK.value());
			response.setContentLengthLong(blob.length());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			if ("GET".equals(request.getMethod())) {
				try (FileInputStream fis = new FileInputStream(blob.path().toFile())) {
					IOUtils.copy(fis, response.getOutputStream());
				}
			}
		}
	}

}
