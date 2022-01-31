package dev.pinchflat.anemoi.registry.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class BaseController {

	@GetMapping("/v2")
	@ResponseStatus(HttpStatus.OK)
	public void getBase() {
		return;
	}
	
}
