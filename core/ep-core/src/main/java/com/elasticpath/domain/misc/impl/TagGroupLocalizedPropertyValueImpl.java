package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for TagGroup.
 */
@Entity
@DiscriminatorValue("TagGroup")
public class TagGroupLocalizedPropertyValueImpl extends
		AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = -1178516969977275763L;
}
