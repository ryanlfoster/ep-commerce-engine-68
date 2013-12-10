/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.service;

import java.util.Set;

import javax.validation.ConstraintViolation;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * Convenience methods for BVal validation.
 */
public interface ValidatorUtils {

	/**
	 * Validate an attribute value.<br>
	 * If there are any violations, it takes the first violation and compares the violation constraint descriptor annotation type against the
	 * AttributeValueWithType custom class Size annotations. <br>
	 * If there is a match, then an EpTooLongBindException is thrown. <br>
	 * If an unidentified constraint descriptor annotation type is found, then an EpBindException is thrown.<br>
	 * If there are no violations, then no exceptions are thrown.
	 *
	 * @param attributeValue the attribute value
	 * @throws EpBindException the ep bind exception
	 */
	void validateAttributeValue(final AttributeValue attributeValue) throws EpBindException;

	/**
	 * Validate the given object.<br>
	 * Delegates to the underlying Validator#validate method.
	 *
	 * @param <T> the generic type
	 * @param object the object
	 * @param groups the groups
	 * @return the sets the
	 */
	<T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups);
	
	/**
	 * Join violation messages.
	 *
	 * @param <T> the generic type
	 * @param constraintViolations the constraint violations
	 * @return the string
	 */
	<T> String joinViolationMessages(Set<ConstraintViolation<T>> constraintViolations);
	
}
