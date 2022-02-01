package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import dev.pinchflat.anemoi.registry.controller.response.BaseResponse;

class BaseControllerTest {

	private final BaseController baseController = new BaseController();
	
	@Test
	void testGetBase() {
		BaseResponse actual = baseController.getBase();
		assertNotNull(actual);
	}

}
