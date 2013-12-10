/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import com.elasticpath.domain.attribute.AttributeValueWithType;

/**
 * The AttributeRangeFilter represents the attribute filter with a range predefined.
 */
public interface AttributeRangeFilter extends RangeFilter<AttributeRangeFilter, AttributeValueWithType>,
		AttributeFilter<AttributeRangeFilter> {
}
