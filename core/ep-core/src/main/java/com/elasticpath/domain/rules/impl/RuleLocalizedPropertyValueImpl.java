/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.domain.rules.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.elasticpath.domain.misc.impl.AbstractLocalizedPropertyValueImpl;

/**
 * Subclass holder of localized property value for a {@code Rule}.
 */
@Entity
@DiscriminatorValue("Rule")
public class RuleLocalizedPropertyValueImpl extends AbstractLocalizedPropertyValueImpl {
	private static final long serialVersionUID = 3373887544639340839L;
}
