package dev.pinchflat.anemoi.registry.controller;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pinchflat.anemoi.registry.controller.request.GetManifestRequest;
import dev.pinchflat.anemoi.registry.service.ManifestService;
import dev.pinchflat.anemoi.registry.service.data.Manifest;

@RestController
public class ManifestController {
	
	@NotNull
	private final Logger logger = LoggerFactory.getLogger(ManifestController.class);


	@NonNull
	private final ManifestService manifestService;

	@Autowired
	protected ManifestController(@NonNull ManifestService manifestService) {
		super();
		this.manifestService = manifestService;
	}

	@GetMapping("/v2/**/manifests/*")
	public void getManifest(@NotNull GetManifestRequest request, //
			@NotNull HttpServletResponse response) throws IOException {
		final Manifest manifest= manifestService.get(request.getManifestKey());
		
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(manifest.getMediaType());
		if (request.isDataRequested()) {
			try {
				try (FileInputStream fis = new FileInputStream(manifest.getPath().toFile())) {
					IOUtils.copy(fis, response.getOutputStream());
				}
				response.flushBuffer();
			} catch (IOException e) {
				if (response.isCommitted()) {
					logger.trace("Did not write to response since already committed");
					return;
				}
				throw e;
			}
		}
	}
}
