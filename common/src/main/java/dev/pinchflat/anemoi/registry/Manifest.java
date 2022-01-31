package dev.pinchflat.anemoi.registry;

import java.nio.file.Path;

public record Manifest(Id id, Path path, String contentType, String digest) {

}
