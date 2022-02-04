package dev.pinchflat.anemoi.registry.controller.resolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Qualifier("anemoi")
abstract class AbstractMethodArgumentResolver<T> implements HandlerMethodArgumentResolver {

	private final Class<T> supportedType;
	private final Optional<Class<? extends Annotation>> parameterAnnotation;

	protected AbstractMethodArgumentResolver(Class<T> supportedType) {
		this(supportedType, null);
	}

	protected AbstractMethodArgumentResolver(Class<T> supportedType, Class<? extends Annotation> parameterAnnotation) {
		this.supportedType = supportedType;
		this.parameterAnnotation = Optional.ofNullable(parameterAnnotation);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean supported = parameter.getParameterType().equals(supportedType);
		if (supported && parameterAnnotation.isPresent()) {
			supported = parameter.getParameterAnnotation(parameterAnnotation.get()) != null;
		}
		return supported;
	}

	protected boolean isPathParser() {
		return false;
	}

	protected boolean isBodyParser() {
		return false;
	}

	protected T parse(String requestPath) {
		return null;
	}

	protected T parse(Path path, long size, String contentRange, String contentLength) {
		return null;
	}

	private T parseBody(HttpServletRequest request) throws IOException {
		File f = File.createTempFile("prm", "test");
		f.deleteOnExit();
		long size = 0;
		try (FileOutputStream fos = new FileOutputStream(f)) {
			size = IOUtils.copy(request.getInputStream(), fos);
			fos.flush();
		}
		if (size > 0) {
			String contentRange = request.getHeader("Content-Range");
			String contentLength = request.getHeader("Content-Length");
			return parse(f.toPath(), size, contentRange, contentLength);
		}
		return null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		if (isPathParser()) {
			return parse(request.getRequestURI());
		} else if (isBodyParser()) {
			return parseBody(request);
		}
		return null;
	}

}
