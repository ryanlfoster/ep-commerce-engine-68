/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for Shipping Service Level.
 */
@Entity
@DiscriminatorValue("ShippingServiceLevel")
public class ShippingServiceLevelLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = 6736567770875833717L;
}
