package dev.pinchflat.anemoi.registry.controller.resolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.Id;

@Component
public class IdMethodArgumentResolver extends AbstractMethodArgumentResolver<Id> {

	private final Pattern uriPattern = Pattern
			.compile("\\/v2\\/(?<name>.*)\\/(blobs(\\/uploads)*|manifests)\\/(?<reference>.*)");

	protected IdMethodArgumentResolver(Class<?> supportedType) {
		super(Id.class);
	}

	@Override
	protected boolean isPathParser() {
		return true;
	}

	@Override
	protected Id parse(String requestPath) {
		final Matcher matcher = uriPattern.matcher(requestPath);
		if (matcher.find()) {
			return new Id(matcher.group("repository"), matcher.group("reference"));
		}
		throw new IllegalArgumentException(requestPath+ " is not supported by id pattern");
	}
}
