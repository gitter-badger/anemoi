package dev.pinchflat.anemoi.registry.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BaseController.class)
@ContextConfiguration(classes = { BaseController.class })
public class BaseControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "test", password = "pass", roles = "USER")
	void returnOkForAuthenticatedUser() throws Exception {
		this.mockMvc //
				.perform(get("/v2")) //
				.andExpect(status().isOk()) //
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	@WithAnonymousUser
	void returnUnauthorizedForAnonUser() throws Exception {
		this.mockMvc //
				.perform(get("/v2")) //
				.andExpect(status().isUnauthorized());
	}

}
