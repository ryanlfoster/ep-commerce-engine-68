/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.changeset.impl;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * Guid resolver for SkuOptionValues. 
 */
public class SkuOptionValueGuidResolver implements ObjectGuidResolver {

	@Override
	public String resolveGuid(final Object object) {
		if (object instanceof SkuOptionValue) {
			SkuOptionValue skuOptionValue = (SkuOptionValue) object;
			return skuOptionValue.getOptionValueKey();
		}
		return null;
	}

	@Override
	public boolean isSupportedObject(final Object object) {
		return object instanceof SkuOptionValue;
	}
	
}
