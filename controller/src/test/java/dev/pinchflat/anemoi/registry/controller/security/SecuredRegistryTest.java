package dev.pinchflat.anemoi.registry.controller.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import dev.pinchflat.anemoi.registry.controller.BaseController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BaseController.class)
@TestPropertySource(locations = "classpath:secured.properties")
@ContextConfiguration(classes = { //
		BaseController.class, //
		SecurityConfiguration.class, //
		AnemoiWebSecurityConfigurerAdapter.class//
})
class SecuredRegistryTest {
	
	@Autowired
	MockMvc mockMvc;

	@Test
	@WithAnonymousUser
	void testAnonUser() throws Exception {
		mockMvc.perform(get("/v2")).andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(username = "test", password = "pass", roles = "user")
	void testValidUser() throws Exception {
		mockMvc.perform(get("/v2")).andExpect(status().isOk());
	}

}
