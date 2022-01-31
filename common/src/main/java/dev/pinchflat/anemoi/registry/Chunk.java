package dev.pinchflat.anemoi.registry;

import java.nio.file.Path;

public record Chunk(Path path, long offsetStart, long offsetEnd, long size){

	public String getRange() {
		return offsetStart+"-"+offsetEnd;
	}
}
