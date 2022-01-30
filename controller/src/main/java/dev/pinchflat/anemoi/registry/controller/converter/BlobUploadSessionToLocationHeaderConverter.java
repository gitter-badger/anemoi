package dev.pinchflat.anemoi.registry.controller.converter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.service.data.BlobUploadSession;

@Component
@Qualifier("locationHeader")
public final class BlobUploadSessionToLocationHeaderConverter implements Converter<BlobUploadSession, String>{

	@Override
	public String convert(BlobUploadSession source) {
		if (source == null)
			return null;
		return "/v2/"+source.getBlobUploadSessionKey().getRepository()+"/blobs/uploads/"+source.getBlobUploadSessionKey().getUploadId();
	}

}
