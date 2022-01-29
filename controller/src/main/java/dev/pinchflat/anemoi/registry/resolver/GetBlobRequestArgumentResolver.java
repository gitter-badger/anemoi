package dev.pinchflat.anemoi.registry.resolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.pinchflat.anemoi.registry.digest.DigestType;
import dev.pinchflat.anemoi.registry.exception.InvalidRequestPathMappingException;
import dev.pinchflat.anemoi.registry.request.GetBlobRequest;

@Component
final class GetBlobRequestArgumentResolver implements HandlerMethodArgumentResolver {
	
	@NotNull
	private static final Logger logger = LoggerFactory.getLogger(GetBlobRequestArgumentResolver.class);
	
	@NotNull
	private final Pattern uriPattern = Pattern
			.compile("\\/v2\\/(?<name>.*)\\/blobs\\/(?<digestType>sha256|sha512):(?<digest>[a-z0-9]*)");

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return GetBlobRequest.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		final String path = request.getRequestURI();

		final Matcher matcher = uriPattern.matcher(path);

		if (matcher.find()) {
			return new GetBlobRequest(// @formatter:off
				HttpMethod.HEAD.toString().equals(request.getMethod()),
				matcher.group("name"),
				DigestType.fromPrefix(matcher.group("digestType")), 
				matcher.group("digest") // @formatter:on
			);
		} else {
			InvalidRequestPathMappingException ex = new InvalidRequestPathMappingException(
					"Unsupported Request Uri for GetBlobRequest:"+path);
			logger.error("Unsupported Request url", ex);
			throw ex;
		}
	}

}
