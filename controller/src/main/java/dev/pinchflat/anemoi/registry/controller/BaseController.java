package dev.pinchflat.anemoi.registry.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class BaseController {

	@GetMapping("/v2")
	public ResponseEntity<?> getBase() {
		return ResponseEntity //
				.ok() //
				.contentType(APPLICATION_JSON) //
				.build();
	}

}
