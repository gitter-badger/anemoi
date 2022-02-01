package dev.pinchflat.anemoi.registry;

public record UploadSession(Id id, long offsetStart, long offsetEnd) {

	public String range() {
		return offsetStart+"-"+offsetEnd;
	}
}
