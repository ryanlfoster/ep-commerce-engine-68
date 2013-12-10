package com.elasticpath.domain.geoip.provider.impl;

import com.elasticpath.tags.service.impl.AbstractExternalCSVSelectableTagValueProvider;

/**
 * String implementation of {@link AbstractExternalCSVSelectableTagValueProvider}.
 */
public class NullSelectableStringTagValueProviderImpl extends AbstractExternalCSVSelectableTagValueProvider<String> {

	@Override
	protected String adaptString(final String stringValue) {
		return stringValue;
	}

}
