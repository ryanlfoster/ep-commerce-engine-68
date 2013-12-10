/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for Sku Option Value.
 */
@Entity
@DiscriminatorValue("SkuOptionValue")
public class SkuOptionValueLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = 2270589658948539030L;
}
