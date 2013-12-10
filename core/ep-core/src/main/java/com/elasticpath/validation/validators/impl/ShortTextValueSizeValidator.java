/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.validation.constraints.ShortTextValueSize;

/**
 * Validator for the short text value size for attribute values.
 */
public class ShortTextValueSizeValidator implements ConstraintValidator<ShortTextValueSize, AttributeValueWithType> {

	private int min;

	private int max;

	private List<AttributeType> supportedAttributeTypes;

	@Override
	public void initialize(final ShortTextValueSize constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	/**
	 * {@inheritDoc} <br>
	 * Validates that the short text value is within size constraints.
	 */
	@Override
	public boolean isValid(final AttributeValueWithType attribute, final ConstraintValidatorContext context) {
		AttributeType attributeType = attribute.getAttributeType();
		if (attributeType == null || attribute.getShortTextValue() == null) {
            return true;
        }

		if (supportedAttributeTypes.contains(attributeType)) {
			int length = attribute.getShortTextValue().length();
			if (length > max) {
				buildConstraintViolation(context,
						"{com.elasticpath.com.elasticpath.validation.validators.impl.ShortTextValueSizeValidator.tooLong.message}");
				return false;
			} else if (length < min) {
				buildConstraintViolation(context,
						"{com.elasticpath.com.elasticpath.validation.validators.impl.ShortTextValueSizeValidator.tooShort.message}");
				return false;
			}
		}
		return true;
	}

	/**
	 * Builds the constraint violation.
	 *
	 * @param context the context
	 * @param template the template
	 */
	protected void buildConstraintViolation(final ConstraintValidatorContext context, final String template) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(template).addConstraintViolation();
	}

	public void setSupportedAttributeTypes(final List<AttributeType> supportedAttributeTypes) {
		this.supportedAttributeTypes = supportedAttributeTypes;
	}

	protected List<AttributeType> getSupportedAttributeTypes() {
		return supportedAttributeTypes;
	}

}
