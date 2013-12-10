package com.elasticpath.domain.search.query.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class is an annotation mix-in to allow marshalling the {@link SearchTermsImpl}.
 */
@SuppressWarnings("PMD.AbstractNaming")
@JsonPropertyOrder({ "keywords" })
public abstract class SearchTermsImplJacksonMixIn extends SearchTermsImpl {
	private static final long serialVersionUID = 2L;

	@Override
	@JsonProperty("keywords")
	public abstract String getKeywords();
}
