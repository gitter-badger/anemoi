package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import dev.pinchflat.anemoi.registry.controller.config.RegistryWebMvcConfigurer;
import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.error.RegistryException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BaseController.class)
@ContextConfiguration(classes = { //
		RegistryWebMvcConfigurer.class, //
		BaseController.class })
@AutoConfigureMockMvc(addFilters = false)
class BaseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testGet() throws Exception {
		mockMvc//
				.perform(get("/v2/"))//
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void testDefaultHandler() {
		NestedServletException nse = assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/v2/invalid")));
		assertInstanceOf(RegistryException.class, nse.getRootCause());
		RegistryException re= (RegistryException) nse.getCause();
		assertEquals(RegistryErrorType.UNSUPPORTED, re.getRegistryErrorType());

	}
}
