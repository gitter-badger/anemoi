package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.controller.response.GetTagsResponse;
import dev.pinchflat.anemoi.registry.service.TagService;

class TagControllerTest {
	private static final String REPOSITORY_NAME = "REPO";
	private static final long START = 0;
	private static final long COUNT = 2;
	
	private final TagService tagService = mock(TagService.class);
	private final TagController tagController = new TagController(tagService);
			
	@BeforeEach
	void setUp() {
		reset(tagService);
	}
	
	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(tagService);
	}
	
	@Test
	void testGetTags() {
		List<String> tags = List.of("1", "2");
		Pair<Long,List<String>> tagsWithTotalTagCount = Pair.of(1000l, tags);
		
		when(tagService.list(REPOSITORY_NAME, START, COUNT)).thenReturn(tagsWithTotalTagCount);
		
		GetTagsResponse response = tagController.getTags(REPOSITORY_NAME, START, COUNT);
		
		assertNotNull(response);
		assertEquals(COUNT, response.count());
		assertTrue(response.hasNext());
		assertEquals(START+COUNT-1, response.nextPageOffset());
		assertEquals(REPOSITORY_NAME,response.repositoryName());
		assertEquals(START, response.start());
		assertEquals(tags, response.tags());
		
		verify(tagService,times(1)).list(REPOSITORY_NAME, START, COUNT);
	}

}
