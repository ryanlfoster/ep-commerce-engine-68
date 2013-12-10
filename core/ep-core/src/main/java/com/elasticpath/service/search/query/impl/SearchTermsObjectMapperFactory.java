package com.elasticpath.service.search.query.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.elasticpath.domain.search.query.impl.SearchTermsImpl;
import com.elasticpath.domain.search.query.impl.SearchTermsImplJacksonMixIn;

/**
 * A factory for creating an {@link ObjectMapper} for {@link com.elasticpath.domain.search.query.SearchTerms} objects.
 */
public final class SearchTermsObjectMapperFactory {
	private static final ObjectMapper OBJECT_MAPPER;

	private SearchTermsObjectMapperFactory() {
		//no-op
	}

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.addMixInAnnotations(SearchTermsImpl.class, SearchTermsImplJacksonMixIn.class);
	}

	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}
}
