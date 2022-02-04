package dev.pinchflat.anemoi.registry.controller;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dev.pinchflat.anemoi.registry.controller.config.RegistryWebMvcConfigurer;
import dev.pinchflat.anemoi.registry.controller.resolver.RepositoryNameMethodArgumentResolver;
import dev.pinchflat.anemoi.registry.service.TagList;
import dev.pinchflat.anemoi.registry.service.TagService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TagController.class)
@ContextConfiguration(classes = { //
		RegistryWebMvcConfigurer.class, //
		TagController.class, //
		RepositoryNameMethodArgumentResolver.class})
class TagControllerTest {
	private static final String REPOSITORY = "REPOSITORY";
	private static final String REPOSITORY_WITH_NS = "NS/REPOSITORY";
	private static final String REPOSITORY_WITH_MULTI_NS = "NS/NS2/REPOSITORY";

	@MockBean
	TagService tagService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		Mockito.reset(tagService);
	}

	@AfterEach
	void tearDown() {
		Mockito.verifyNoMoreInteractions(tagService);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS,REPOSITORY_WITH_MULTI_NS })
	void testGetTags(String repositoryName) throws Exception {
		TagList tagList = new TagList(repositoryName,0,2,2l, List.of("A", "B"));
		Mockito.when(tagService.list(repositoryName, -1, -1)).thenReturn(tagList);

		mockMvc//
				.perform(get("/v2/" + repositoryName + "/tags/list"))//
				.andExpect(MockMvcResultMatchers.status().isOk())//
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))//
				.andExpect(MockMvcResultMatchers.content().json("['A','B']"));

		Mockito.verify(tagService, times(1)).list(repositoryName, -1, -1);
	}

	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS,REPOSITORY_WITH_MULTI_NS })
	void testGetTagsPaginated(String repositoryName) throws Exception {
		TagList tagList = new TagList(repositoryName,0,2,100l, List.of("A", "B"));
		Mockito.when(tagService.list(repositoryName, 0, 2)).thenReturn(tagList);

		mockMvc//
				.perform(get("/v2/" + repositoryName + "/tags/list?n=2&last=0"))//
				.andExpect(MockMvcResultMatchers.status().isOk())//
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))//
				.andExpect(MockMvcResultMatchers.header().string("Link", "/v2/"+repositoryName+"/tags/list?n=2&last=1; rel=\"next\""))//
				.andExpect(MockMvcResultMatchers.content().json("['A','B']"));

		Mockito.verify(tagService, times(1)).list(repositoryName, 0, 2);
	}
	
	/*
	@ParameterizedTest
	@ValueSource(strings = { REPOSITORY, REPOSITORY_WITH_NS,REPOSITORY_WITH_MULTI_NS })
	void testAccessDenied(String repositoryName) throws Exception {
		Mockito.when(tagService.list(repositoryName, -1, -1)).thenThrow(AccessDeniedException.class);

		invokeFailureEndpoint(//
				get("/v2/" + repositoryName + "/tags/list"),//
				403, //
				"DENIED", // 
				"requested access to the resource is denied");

		Mockito.verify(tagService, times(1)).list(repositoryName, -1, -1);
	}

	private void invokeFailureEndpoint(RequestBuilder rb, int status, String code, String message)
			throws Exception {
		mockMvc//
				.perform(rb)//
				.andExpect(MockMvcResultMatchers.status().is(status))//
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))//
				.andExpect(MockMvcResultMatchers.content().json("{'errors':[{'code':'"+code+"','message':'"+message+"'}]}"));
	}
	*/
}
