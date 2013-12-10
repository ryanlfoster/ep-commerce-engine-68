package com.elasticpath.validation.service;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * Summarises a set of validation constraint violations into a single string. 
 */
public interface ConstraintViolationsSummariser {
	
	/**
	 * Summarises the constraint violations.
	 *
	 * @param <T> the generic type
	 * @param violations the violations
	 * @return the string
	 */
	<T> String summarise(Set<ConstraintViolation<T>> violations);
}
