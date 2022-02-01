package dev.pinchflat.anemoi.registry.controller;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryName;
import dev.pinchflat.anemoi.registry.controller.response.GetTagsResponse;
import dev.pinchflat.anemoi.registry.service.TagService;

@RestController
final class TagController {

	private final TagService tagService;

	@Autowired
	protected TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping(path = "/v2/**/tags/list")
	public GetTagsResponse getTags(//
			@RepositoryName String repositoryName, //
			@RequestParam(name = "last", defaultValue = "-1") long start, //
			@RequestParam(name = "n", defaultValue = "-1") long count) {

		Pair<Long, List<String>> tags = tagService.list(repositoryName, start, count);

		return new GetTagsResponse(repositoryName, start, count, tags.getKey(), tags.getValue());
	}

}
