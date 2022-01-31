package dev.pinchflat.anemoi.registry;

public record UploadSession(Id id, long offsetStart, long offsetEnd) {

	public String getRange() {
		return offsetStart+"-"+offsetEnd;
	}
}
