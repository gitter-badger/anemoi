package dev.pinchflat.anemoi.registry.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.error.RegistryErrorType;
import dev.pinchflat.anemoi.registry.error.RegistryException;

@RestController
public final class BaseController {

	@GetMapping("/v2")
	@ResponseStatus(HttpStatus.OK)
	public void getBase() {
		return;
	}
	
	@RequestMapping(path = "/v2/**")
	public void defaultHandler() {
		throw new RegistryException(RegistryErrorType.UNSUPPORTED);
	}
}
