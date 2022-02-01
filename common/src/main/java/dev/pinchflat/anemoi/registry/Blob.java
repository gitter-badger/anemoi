package dev.pinchflat.anemoi.registry;

import java.nio.file.Path;

public record Blob (Id id, Path path, long length, Blob source) {

	public Blob(Id id, Path path, long length) {
		this(id,path,length,null);
	}
	
	public Blob(Id id, Blob source) {
		this(id,null,0,source);
	}
	
	public boolean mounted() {
		return source() != null;
	}
	
	public String range() {
		return "0-"+Math.max(0,length-1);
	}
}
