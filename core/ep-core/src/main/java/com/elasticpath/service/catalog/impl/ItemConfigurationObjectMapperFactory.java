package com.elasticpath.service.catalog.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.elasticpath.domain.catalog.impl.ItemConfigurationImpl;
import com.elasticpath.domain.catalog.impl.ItemConfigurationImplJacksonMixIn;

/**
 * The factory for {@link ObjectMapper} that is used for marshalling {@link ItemConfigurationImpl}.
 */
public final class ItemConfigurationObjectMapperFactory {
	private static final ObjectMapper OBJECT_MAPPER;

	private ItemConfigurationObjectMapperFactory() {
		//no-op
	}

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.addMixInAnnotations(ItemConfigurationImpl.class, ItemConfigurationImplJacksonMixIn.class);
	}

	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}
}
