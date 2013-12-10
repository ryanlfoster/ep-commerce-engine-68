/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.validation.constraints.ShortTextMultiValuesElementSize;

/**
 * Validator for the short text multivalue element size for attribute values.
 */
public class ShortTextMultiValuesElementSizeValidator implements ConstraintValidator<ShortTextMultiValuesElementSize, AttributeValueWithType> {

	private int min;

	private int max;

	@Override
	public void initialize(final ShortTextMultiValuesElementSize constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	/**
	 * {@inheritDoc} <br>
	 * Validates that the short text multivalue elements are within size constraints.
	 */
	@Override
	public boolean isValid(final AttributeValueWithType attributeValue, final ConstraintValidatorContext context) {
		if (attributeValue.getAttributeType() == null || attributeValue.getAttribute() == null
				|| !(isAttributeValueShortTextMultiValues(attributeValue)) || attributeValue.getShortTextMultiValues() == null) {
			return true;
		}

		List<String> values = attributeValue.getShortTextMultiValues();
		boolean valid = true;
		long index = 0;
		for (String item : values) {
			if (isElementTooLong(item)) {
				buildConstraintViolation(context,
						"{com.elasticpath.com.elasticpath.validation.validators.impl.ShortTextMultiValuesElementSizeValidator.tooLong.message}",
						index);
				valid = false;
			} else if (isElementTooShort(item)) {
				buildConstraintViolation(context,
						"{com.elasticpath.com.elasticpath.validation.validators.impl.ShortTextMultiValuesElementSizeValidator.tooShort.message}",
						index);
				valid = false;
			}

			index++;
		}

		return valid;
	}

	private boolean isAttributeValueShortTextMultiValues(final AttributeValueWithType attributeValue) {
		return AttributeType.SHORT_TEXT == attributeValue.getAttributeType() && attributeValue.getAttribute().isMultiValueEnabled();
	}

	private boolean isElementTooLong(final String token) {
		if (token == null) {
			return true;
		}
		int length = token.length();
		return length > max;
	}

	private boolean isElementTooShort(final String token) {
		if (token == null) {
			return true;
		}
		int length = token.length();
		return length < min;
	}

	/**
	 * Builds the constraint violation.
	 *
	 * @param context the context
	 * @param template the template
	 * @param index the index
	 */
	protected void buildConstraintViolation(final ConstraintValidatorContext context, final String template, final long index) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(template).addNode(String.format("[%d]", index))
				.addConstraintViolation();
	}

}
