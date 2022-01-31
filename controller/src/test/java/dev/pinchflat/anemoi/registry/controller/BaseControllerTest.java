package dev.pinchflat.anemoi.registry.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class BaseControllerTest {

	private final BaseController baseController;
	
	BaseControllerTest() {
		baseController = new BaseController();
	}
	
	@Test
	void isBaseCallValid() {
		assertDoesNotThrow(()->{
			baseController.getBase();	
		});
	}

}
