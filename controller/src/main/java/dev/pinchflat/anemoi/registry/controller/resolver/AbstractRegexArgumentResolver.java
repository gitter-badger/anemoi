package dev.pinchflat.anemoi.registry.controller.resolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public abstract class AbstractRegexArgumentResolver<T> implements HandlerMethodArgumentResolver {

	@NonNull
	private final Class<T> clazz;
	
	@NotNull
	private final Pattern uriPattern;
	
	@NonNull
	private final List<String> matchParamNames;

	protected AbstractRegexArgumentResolver(
			@NonNull Class<T> clazz, 
			@NonNull String regex,
			@NonNull String...matchParameters) {
		this.clazz = clazz;
		this.uriPattern = Pattern.compile(regex);
		this.matchParamNames=Arrays.asList(matchParameters);
	}

	protected abstract T createObject(HttpServletRequest request, Map<String, String> matchParams) throws IOException;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return clazz.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		final String path = request.getRequestURI();
		
		final Matcher matcher = uriPattern.matcher(path);
		matcher.find();
		
		Map<String,String> matchParams = new HashMap<>();
		matchParamNames.forEach(param->matchParams.put(param, matcher.group(param)));
		
		return createObject(request, matchParams);
	}

}
