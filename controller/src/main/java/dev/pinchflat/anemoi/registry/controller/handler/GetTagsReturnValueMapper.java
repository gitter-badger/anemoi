package dev.pinchflat.anemoi.registry.controller.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.controller.response.GetTagsResponse;

@Component
public class GetTagsReturnValueMapper extends AbstractReturnValueHandler<GetTagsResponse> {

	private static final String LINK_FORMAT = "%s?n=%d&last=%d; rel=\"next\"";

	protected GetTagsReturnValueMapper() {
		super(GetTagsResponse.class);
	}

	@Override
	public Map<String, String> getHeaders(String path, GetTagsResponse value) {
		final Map<String,String> headers= new HashMap<>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		if (value.hasNext()) {
			headers.put("Link", String.format(LINK_FORMAT, path, value.count(), value.nextPageOffset()));
		}
		return headers;
	}

	@Override
	public List<String> getResponseObject(String method, GetTagsResponse value) {
		return value.tags();
	}
}
