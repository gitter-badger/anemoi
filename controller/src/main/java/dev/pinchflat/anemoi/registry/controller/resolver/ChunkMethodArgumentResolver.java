package dev.pinchflat.anemoi.registry.controller.resolver;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.Chunk;

@Component
public class ChunkMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("(?<start>\\d+)-(?<end>\\d+)");

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Chunk.class.equals(parameter.getParameterType()); 
				
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		File f = File.createTempFile("prm", "test");
		long size = 0;
		try(FileOutputStream fos = new FileOutputStream(f)){
			size = IOUtils.copy(request.getInputStream(), fos);
			fos.flush();
		}
		if (size > 0) {
			long startOffset = 0;
			long endOffset = 0;
			String contentRange = request.getHeader("Content-Range");
			String contentLength = request.getHeader("Content-Length");
			if (contentRange != null && contentLength != null) {
				Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);
				if (matcher.find()) {
					startOffset=Long.valueOf(matcher.group("start"));
					endOffset=Long.valueOf(matcher.group("end"));
					size=Long.valueOf(contentLength);
				}
			}
			return new Chunk(f.toPath(), startOffset, endOffset, size);
		}
		return null;
	}

}
