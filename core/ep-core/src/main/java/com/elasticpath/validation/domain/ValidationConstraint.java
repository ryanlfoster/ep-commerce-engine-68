package com.elasticpath.validation.domain;

import java.util.Locale;

/**
 * Validator object represents the constraints that are passed to the validation engine
 * together with a value to validate in order to determine if value provided
 * complies with the validator constraints.
 */
public interface ValidationConstraint {

	/**
	 * @return declarative constraint
	 */
	String getConstraint();
	
	/**
	 * Sets validation constraint.
	 * 
	 * @param constraint VALANG string constraint
	 */
	void setConstraint(final String constraint);
	
	/**
	 * Sets unique identifier.
	 * @param uidPk unique identifier
	 */
	void setUidPk(long uidPk);
	
	/**
	 * @return gets unique identifier.
	 */
	long getUidPk();
	
	/**
	 * Sets validation error message key.
	 * @param errorMessageKey validation error message key
	 */
	void setErrorMessageKey(String errorMessageKey);
	
	/**
	 * @return validation error message key
	 */
	String getErrorMessageKey();

	/**
	 * Returns localized error message, If no message was found, error message key is returned.
	 * 
	 * @param locale Locale
	 * @return localized error message
	 */
	String getLocalizedErrorMessage(final Locale locale);
	
}
