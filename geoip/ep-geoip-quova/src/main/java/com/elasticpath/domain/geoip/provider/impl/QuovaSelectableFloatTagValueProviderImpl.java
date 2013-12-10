package com.elasticpath.domain.geoip.provider.impl;

import com.elasticpath.tags.service.impl.AbstractExternalCSVSelectableTagValueProvider;

/**
 * Float implementation of {@link AbstractExternalCSVSelectableTagValueProvider}.
 */
public class QuovaSelectableFloatTagValueProviderImpl  extends AbstractExternalCSVSelectableTagValueProvider<Float> {
	
	@Override
	protected Float adaptString(final String stringValue) {
		return Float.valueOf(stringValue);
	}
	

}
