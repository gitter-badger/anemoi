package dev.pinchflat.anemoi.registry.controller.response;

import java.nio.file.Path;

public record GetBlobResponse(String repository, String reference, long length, Path path, boolean mounted) {

}
