package dev.pinchflat.anemoi.registry.controller.converter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import dev.pinchflat.anemoi.registry.service.data.BlobKey;

@Component
@Qualifier("locationHeader")
public final class BlobKeyToLocationHeaderConverter implements Converter<BlobKey, String>{

	@Override
	public String convert(BlobKey source) {
		if (source == null)
			return null;
		return "/v2/"+source.getRepository()+"/blobs/"+source.getDigestType().getPrefix()+":"+source.getDigest();
	}

}
