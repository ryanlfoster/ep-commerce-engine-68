package com.elasticpath.validation.domain.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Implementation of DeclarativeValidationConstraint for TagDefinition.
 */
@Entity
@DiscriminatorValue("TagValueType")
public class TagValueTypeDeclarativeValidationConstraintImpl extends
		DeclarativeValidationConstraintImpl {
	private static final long serialVersionUID = -4784153094497992476L;
}
