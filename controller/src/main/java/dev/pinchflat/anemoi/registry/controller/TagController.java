package dev.pinchflat.anemoi.registry.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryName;
import dev.pinchflat.anemoi.registry.service.TagList;
import dev.pinchflat.anemoi.registry.service.TagService;

@RestController
public final class TagController {

	private static final String LINK_FORMAT = "/v2/%s/tags/list?n=%d&last=%d; rel=\"next\"";
	
	private final TagService tagService;
	
	@Autowired
	protected TagController(TagService tagService) {
		super();
		this.tagService = tagService;
	}

	@GetMapping(path = "/v2/**/tags/list")
	public ResponseEntity<List<String>> getTags(//
			@RepositoryName String repositoryName, //
			@RequestParam(name = "last", defaultValue = "-1") long start, //
			@RequestParam(name = "n", defaultValue = "-1") long count) {
		
		TagList tagList = tagService.list(repositoryName, start, count);
		var bodyBuilder = ResponseEntity.//
				ok().//
				header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		if (tagList.hasNext()) {
			bodyBuilder.header("Link", String.format(LINK_FORMAT, tagList.repositoryName(), tagList.count(), tagList.nextPageOffset()));
		}
		return bodyBuilder.body(tagList.tags());
	}

}
