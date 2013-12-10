/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpTooLongBindException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.validation.constraints.LongTextValueSize;
import com.elasticpath.validation.constraints.ShortTextMultiValuesElementSize;
import com.elasticpath.validation.constraints.ShortTextValueSize;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Convenience methods for BVal validation.
 */
public class ValidatorUtilsImpl implements ValidatorUtils {

	private Validator validator;

	@Override
	public void validateAttributeValue(final AttributeValue attributeValue) {
		Set<ConstraintViolation<AttributeValue>> violations = getValidator().validate(attributeValue);
		if (violations.isEmpty()) {
			return;
		} else {
			ConstraintViolation<AttributeValue> violation = violations.iterator().next();
			Class<? extends Object> clazz = violation.getConstraintDescriptor().getAnnotation().annotationType();

			// identify current validators for size to mimic validation that was used within csv import.
			// currently only max length is validated against, otherwise handle as unknown type of validation
			if (ShortTextValueSize.class.equals(clazz) || LongTextValueSize.class.equals(clazz)
					|| ShortTextMultiValuesElementSize.class.equals(clazz)) {
				throw new EpTooLongBindException(violation.getMessage());
			} else {
				throw new EpBindException(violation.getMessage());
			}
		}
	}
	
	@Override
	public <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
		return getValidator().validate(object, groups);
	}
	
	@Override
	public <T> String joinViolationMessages(final Set<ConstraintViolation<T>> constraintViolations) {
		List<String> violationsMessages = new ArrayList<String>(constraintViolations.size());
		for (ConstraintViolation<T> violation : constraintViolations) {
			violationsMessages.add(violation.getMessage());
		}
		return StringUtils.join(violationsMessages, ", ");
	}
	
	public Validator getValidator() {
		return validator;
	}

	public void setValidator(final Validator validator) {
		this.validator = validator;
	}

}
