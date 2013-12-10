package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for TagGroup.
 */
@Entity
@DiscriminatorValue("TagValueConstraint")
public class TagValueConstraintLocalizedPropertyValueImpl extends
		AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = -1720836058301193894L;
}
