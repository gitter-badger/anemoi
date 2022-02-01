package dev.pinchflat.anemoi.registry.controller.resolver;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.Chunk;

@Component
public class ChunkMethodArgumentResolver extends AbstractMethodArgumentResolver<Chunk> {

	private static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("(?<start>\\d+)-(?<end>\\d+)");

	protected ChunkMethodArgumentResolver() {
		super(Chunk.class);
	}
	
	@Override
	protected boolean isBodyParser() {
		return true;
	}

	@Override
	protected Chunk parse(Path path, long size, String contentRange, String contentLength) {
		long startOffset = 0;
		long endOffset = 0;
		if (contentRange != null && contentLength != null) {
			Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);
			if (matcher.find()) {
				startOffset=Long.valueOf(matcher.group("start"));
				endOffset=Long.valueOf(matcher.group("end"));
				size=Long.valueOf(contentLength);
			}
		}
		return new Chunk(path, startOffset, endOffset, size);
	}
	
}
