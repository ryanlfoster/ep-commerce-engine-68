package com.elasticpath.validation.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.validation.service.ConstraintViolationsSummariser;

/**
 * Creates a summary of the violations. 
 */
public class SimpleConstraintViolationsSummariser implements ConstraintViolationsSummariser {

	@Override
	public <T> String summarise(final Set<ConstraintViolation<T>> violations) {
		if (CollectionUtils.isEmpty(violations)) {
			return "Validation succeeded. ";
		}
		
		StringBuffer summary = new StringBuffer();
		for (ConstraintViolation<T> violation : violations) {
			summary.append("\n\t\tThe property: ")
				.append(getPropertyName(violation))
				.append(", ")
				.append(violation.getMessage())
				.append(", provided value = ");
			Object invalidValue = getInvalidValue(violation);
			if (invalidValue == null) {
				summary.append("null");
			} else {
				summary.append('\'')
					.append(invalidValue)
					.append('\'');
			}
		}
		
		return summary.toString();
	}

	/**
	 * Gets the invalid value.
	 *
	 * @param <T> the generic type
	 * @param violation the violation
	 * @return the invalid value
	 */
	protected <T> Object getInvalidValue(final ConstraintViolation<T> violation) {
		return violation.getInvalidValue();
	}

	/**
	 * Gets the property name.
	 *
	 * @param <T> the generic type
	 * @param violation the violation
	 * @return the property name
	 */
	protected <T> String getPropertyName(final ConstraintViolation<T> violation) {
		return String.valueOf(violation.getPropertyPath());
	}

}
