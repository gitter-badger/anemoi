package dev.pinchflat.anemoi.registry.controller.resolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class RepositoryNameMethodArgumentResolver extends AbstractMethodArgumentResolver<String> {

	private final Pattern uriPattern = Pattern.compile("\\/v2\\/(?<r>.*)\\/((blobs\\/uploads)|(tags\\/list))");

	protected RepositoryNameMethodArgumentResolver() {
		super(String.class, RepositoryName.class);
	}

	@Override
	protected boolean isPathParser() {
		return true;
	}

	@Override
	protected String parse(String requestPath) {
		final Matcher matcher = uriPattern.matcher(requestPath);
		if (matcher.find()) {
			return matcher.group("r");
		}
		throw new IllegalArgumentException(requestPath + " is not supported by repository name pattern.");
	}
}
