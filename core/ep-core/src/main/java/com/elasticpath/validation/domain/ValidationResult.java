package com.elasticpath.validation.domain;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * Object that describes the result of validation.
 */
public interface ValidationResult {

	/** default result for valid outcome. */
	ValidationResult VALID = new ValidationResult() {
		
		@Override
		public boolean isValid() {
			return true;
		}
				
		@Override
		public String toString() {
			return "VALID";
		}

		@Override
		public ValidationError[] getErrors() {
			return new ValidationError[0];
		}
		
		/**
		 * @return Empty string since there is no error if this result is returned.
		 */
		public String getMessage() {
			return StringUtils.EMPTY;
		}

		/**
		 * @return Empty string since there is no error if this result is returned.
		 */
		public String getMessage(final Locale locale) {
			return StringUtils.EMPTY;
		}
		
	};

	/**
	 * @return true if the validation outcome does not return any errors.
	 */
	boolean isValid();
	
	/**
	 * @return collection of errors (immutable) if the validation result is invalid.
	 *         if there are no errors then represents an empty array (never null).
	 */
	ValidationError[] getErrors();
	
	/**
	 * @return generic raw message provided during validation, not suitable for UI level. 
	 * For more detailed error message need to examine all error messages in the error collection. 
	 */
	String getMessage();
	
	/**
	 * @param locale the locale for which to generate message.
	 * @return generic human readable localized message suitable for UI.
	 * For more detailed error message need to examine all error messages in the error collection.
	 */
	String getMessage(Locale locale);
	
	
}
