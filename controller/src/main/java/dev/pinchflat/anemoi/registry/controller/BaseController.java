package dev.pinchflat.anemoi.registry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.controller.response.BaseResponse;

@RestController
final class BaseController {

	@GetMapping("/v2")
	public BaseResponse getBase() {
		return new BaseResponse();
	}
	
}
