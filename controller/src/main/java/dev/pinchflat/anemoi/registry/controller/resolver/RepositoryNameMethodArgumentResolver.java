package dev.pinchflat.anemoi.registry.controller.resolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class RepositoryNameMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final Pattern uriPattern = Pattern.compile("\\/v2\\/(?<repository>.*)\\/blobs\\/uploads");

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(RepositoryName.class) != null; 
				
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		final String path = request.getRequestURI();

		final Matcher matcher = uriPattern.matcher(path);
		if (matcher.find()) {
			return matcher.group("repository");
		}
		throw new IllegalArgumentException(path + " is not supported by repository name pattern.");
	}

}
