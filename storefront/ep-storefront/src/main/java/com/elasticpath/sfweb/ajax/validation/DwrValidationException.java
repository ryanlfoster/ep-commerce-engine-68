package com.elasticpath.sfweb.ajax.validation;

import java.util.Map;

/**
 * A wrapper for sending validation errors back to client-side js through dwr.
 */
public class DwrValidationException extends RuntimeException {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private Map<String, String> validationErrors = null;
	
	private Object dataObject = null;

	/**
	 * @param validationErrors the map of field names to error messages
	 */
	public DwrValidationException(final Map<String, String> validationErrors) {
		super();
		this.validationErrors = validationErrors;
	}

	/**
	 * @param validationErrors the map of field names to error messages
	 * @param originalException original exception
	 */
	public DwrValidationException(final Map<String, String> validationErrors, final Throwable originalException) {
		super(originalException);
		this.validationErrors = validationErrors;
	}

	/**
	 * @param validationErrors the map of field names to error messages
	 * @param dataObject any data object that you wish to return besides the errors
	 */
	public DwrValidationException(final Map<String, String> validationErrors, final Object dataObject) {
		super();
		this.validationErrors = validationErrors;
		this.dataObject = dataObject;
	}

	/**
	 * @return a map of field names to error messages
	 */
	public Map<String, String> getValidationErrors() {
		return validationErrors;
	}

	/**
	 * @param validationErrors a map of field names to error messages
	 */
	public void setValidationErrors(final Map<String, String> validationErrors) {
		this.validationErrors = validationErrors;
	}

	/**
	 * @return the dataObject
	 */
	public Object getDataObject() {
		return dataObject;
	}

	/**
	 * @param dataObject the dataObject to set
	 */
	public void setDataObject(final Object dataObject) {
		this.dataObject = dataObject;
	}

}
