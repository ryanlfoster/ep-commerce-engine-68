/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean;

import java.util.List;
import java.util.Map;

/**
 * <code>ImportStep5FormBean</code> represents the command object for import form.
 */
public interface ImportStep5FormBean {

	/**
	 * Returns the data fields in the import data file.
	 * 
	 * @return the data fields in the import data file
	 */
	List<?> getDataFields();

	/**
	 * Sets the data fields in the import data file.
	 *
	 * @param dataFields the data fields in the import data file
	 */
	void setDataFields(final List<?> dataFields);

	/**
	 * Returns the required fields for the import data type.
	 *
	 * @return the required fields for the import data type
	 */
	List<?> getRequiredFields();

	/**
	 * Sets the required fields for the import data type.
	 *
	 * @param requiredFields the required fields for the import data type
	 */
	void setRequiredFields(final List<?> requiredFields);

	/**
	 * Returns the optional fields for the import data type.
	 *
	 * @return the optional fields for the import data type
	 */
	List<?> getOptionalFields();

	/**
	 * Sets the optional fields for the import data type.
	 *
	 * @param optionalFields the optional fields for the import data type
	 */
	void setOptionalFields(final List<?> optionalFields);

	/**
	 * Returns the mapping between data fields from data file and import data type fields.
	 *
	 * @return the map between data fields from data file and import data type fields
	 */
	Map<String, ?> getMappedFields();

	/**
	 * Sets the mapping between data fields from data file and import data type fields.
	 *
	 * @param mappedFields map between data fields from data file and import data type fields
	 */
	void setMappedFields(final Map<String, ?> mappedFields);

	/**
	 * Returns a map with values set as int type for those keys that have a value.
	 *
	 * @return map with values set as int type for those keys that have a value
	 */
	Map<String, Integer> retrieveFinalMappedFields();

	/**
	 * Sets the List<?> of validation import errors.
	 *
	 * @param validationResults List<?> of of validation import errors
	 */
	void setValidationResults(final List<?> validationResults);

	/**
	 * Returns the List<?> of validation import errors.
	 *
	 * @return the List<?> of validation import errors
	 */
	List<?> getValidationResults();

	/**
	 * Returns value of the warning confirmed field.
	 * @return value of the warning confirmed field
	 */
	boolean isWarningConfirmed();

	/**
	 * Set value of the warning confirmed field.
	 * @param warningConfirmed value of the warning confirmed field
	 */
	void setWarningConfirmed(boolean warningConfirmed);

	/**
	 * Sets the List<?> of required fields validation.
	 *
	 * @param requiredFieldsValidation the List<?> of required fields validation
	 */
	void setRequiredFieldsValidation(final List<?> requiredFieldsValidation);

	/**
	 * Returns the List<?> of required fields validation.
	 *
	 * @return the List<?> of required fields validation
	 */
	List<?> getRequiredFieldsValidation();

}