/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for TagOperator.
 */
@Entity
@DiscriminatorValue("TagOperator")
public class TagOperatorLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = 2152963772182024561L;
}
