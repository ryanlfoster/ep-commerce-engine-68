package com.elasticpath.validation.validators.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.validation.constraints.AttributeRequired;

/**
 * Validator for checking the required-ness field of {@link Attribute}s for a given type {@link T}.
 *
 * @param <T> type of object dealing with {@link Attribute}s
 */
public abstract class AbstractAttributeRequiredValidator<T> implements ConstraintValidator<AttributeRequired, T> {

	@Override
	public void initialize(final AttributeRequired constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(final T value, final ConstraintValidatorContext context) {
		boolean result = true;
		List<Attribute> attributes = getAttributesToValidate();
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				if (attribute.isRequired() && !isAttributeValid(attribute, value, context)) {
					buildConstraintViolation(context, attribute);
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Builds the constraint violation.
	 *
	 * @param context the context
	 * @param attribute the attribute
	 */
	protected void buildConstraintViolation(final ConstraintValidatorContext context, final Attribute attribute) {
		// use default validation
	}

	/**
	 * Checks whether the given {@link Attribute} is valid on the given {@code value}.
	 *
	 * @param attribute a required {@link Attribute}
	 * @param value value to check
	 * @param context the {@link ConstraintValidatorContext}
	 * @return whether the value is valid
	 */
	public abstract boolean isAttributeValid(Attribute attribute, T value, ConstraintValidatorContext context);

	/**
	 * @return list of {@link Attribute}s to validate against.
	 */
	protected abstract List<Attribute> getAttributesToValidate();
}
