package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.controller.response.GetBaseResponse;

class BaseControllerTest {

	private final BaseController baseController = new BaseController();
	
	@Test
	void testGetBase() {
		GetBaseResponse actual = baseController.getBase();
		assertNotNull(actual);
	}

}
