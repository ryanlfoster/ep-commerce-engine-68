/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of LocalizedPropertyValue for TagDefinition.
 */
@Entity
@DiscriminatorValue("TagDefinition")
public class TagDefinitionLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = -7131694134267258949L;
}
