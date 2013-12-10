package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for TagAllowedValue.
 */
@Entity
@DiscriminatorValue("TagAllowedValue")
public class TagAllowedValueLocalizedPropertyValueImpl extends
		AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = 8037019440159811873L;
}
