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

import dev.pinchflat.anemoi.registry.Id;

@Component
public class IdMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final Pattern uriPattern = Pattern
			.compile("\\/v2\\/(?<name>.*)\\/(blobs(\\/uploads)*|manifests)\\/(?<reference>.*)");

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Id.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		final String path = request.getRequestURI();

		final Matcher matcher = uriPattern.matcher(path);
		if (matcher.find()) {
			return new Id(matcher.group("repository"), matcher.group("reference"));
		}
		throw new IllegalArgumentException(path + " is not supported by id pattern");
	}

}
