/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for Brand.
 */
@Entity
@DiscriminatorValue("Brand")
public class BrandLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {

	private static final long serialVersionUID = 4258471079002423872L;

}
