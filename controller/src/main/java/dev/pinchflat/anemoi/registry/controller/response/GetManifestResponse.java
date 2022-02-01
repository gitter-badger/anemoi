package dev.pinchflat.anemoi.registry.controller.response;

import java.nio.file.Path;

public record GetManifestResponse(String contentType, Path path) {

}
