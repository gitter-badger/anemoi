package dev.pinchflat.anemoi.registry.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import dev.pinchflat.anemoi.registry.controller.response.GetTagsResponse;

class GetTagsReturnValueMapperTest {
	private static final String REPOSITORY = "REPO";

	private final GetTagsReturnValueMapper mapper = new GetTagsReturnValueMapper();

	@Test
	void testGetStatus() {
		assertEquals(HttpStatus.OK, mapper.getStatus(null));
	}

	@Test
	void testGetHeaders() {
		GetTagsResponse gtr = new GetTagsResponse(REPOSITORY,0,100,50,List.of());
		final Map<String, String> expected = Map.of("Content-Type",MediaType.APPLICATION_JSON_VALUE);
		
		final Map<String, String> actual = mapper.getHeaders("/test", gtr);
		assertEquals(expected, actual);
	}
	
	@Test
	void testGetHeadersWithNextPage() {
		GetTagsResponse gtr = new GetTagsResponse(REPOSITORY,0,100,200,List.of());
		final Map<String, String> expected = Map.of(
				"Content-Type",MediaType.APPLICATION_JSON_VALUE,
				"Link", "/test?n=100&last=99; rel=\"next\"");
		
		final Map<String, String> actual = mapper.getHeaders("/test", gtr);
		assertEquals(expected, actual);
	}

	@Test
	void testGetResource() {
		assertNull(mapper.getResource(null, null));
	}

	@Test
	void testGetResponseObject() {
		GetTagsResponse gtr = new GetTagsResponse(REPOSITORY,0,100,50,List.of("A","B"));
		
		List<String> actual = mapper.getResponseObject("GET", gtr);
		
		assertEquals(gtr.tags(),actual);
	}

	@ParameterizedTest
	@MethodSource("supportMethodSource")
	void testSupportsReturnType(Class<?> supportedClazz, boolean expected) {
		MethodParameter p = mock(MethodParameter.class);

		Mockito.when(p.getParameterType()).then(new Answer<Class<?>>() {
			@Override
			public Class<?> answer(InvocationOnMock invocation) throws Throwable {
				return supportedClazz;
			}
		});

		assertEquals(expected, mapper.supportsReturnType(p));

		Mockito.verify(p, Mockito.times(1)).getParameterType();
		Mockito.verifyNoMoreInteractions(p);
	}

	private static Stream<Arguments> supportMethodSource() {
		return Stream.of(Arguments.of(GetTagsResponse.class, true), Arguments.of(String.class, false));
	}
}
