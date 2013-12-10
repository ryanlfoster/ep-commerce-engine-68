/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.validation.constraints.LongTextValueSize;

/**
 * Validator for the long text value size for attribute values.
 */
public class LongTextValueSizeValidator implements ConstraintValidator<LongTextValueSize, AttributeValueWithType> {

	private int min;

	private int max;

	@Override
	public void initialize(final LongTextValueSize constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	/**
	 * {@inheritDoc} <br>
	 * Validates that the long text value is within size constraints.
	 */
	@Override
	public boolean isValid(final AttributeValueWithType attributeValue, final ConstraintValidatorContext context) {
		AttributeType attributeType = attributeValue.getAttributeType();
		if (attributeType == null || !(AttributeType.LONG_TEXT == attributeValue.getAttributeType())
				|| attributeValue.getLongTextValue() == null) {
			return true;
		}

		int length = attributeValue.getLongTextValue().length();
		if (length > max) {
			buildConstraintViolation(context,
					"{com.elasticpath.com.elasticpath.validation.validators.impl.LongTextValueSizeValidator.tooLong.message}");
			return false;
		} else if (length < min) {
			buildConstraintViolation(context,
					"{com.elasticpath.com.elasticpath.validation.validators.impl.LongTextValueSizeValidator.tooShort.message}");
			return false;
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

}
