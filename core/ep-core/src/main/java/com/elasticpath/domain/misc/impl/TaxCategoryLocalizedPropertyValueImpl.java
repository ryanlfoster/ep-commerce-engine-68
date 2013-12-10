/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for Tax Category.
 */
@Entity
@DiscriminatorValue("TaxCategory")
public class TaxCategoryLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = -7599508209589653382L;
}
