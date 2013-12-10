package com.elasticpath.cmweb.formbean.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmweb.formbean.ImportStep5FormBean;

/**
 * This bean represents import form.
 */
public class ImportStep5FormBeanImpl implements ImportStep5FormBean {

	private List<?> dataFields;

	private List<?> requiredFields;

	private List<?> optionalFields;

	private Map<String, ?> mappedFields = new HashMap<String, Object>();

	private List<?> validationResults;

	private List<?> requiredFieldsValidation;

	private boolean warningConfirmed;

	/**
	 * Returns the data fields in the import data file.
	 * 
	 * @return the data fields in the import data file
	 */
	public List<?> getDataFields() {
		return dataFields;
	}

	/**
	 * Sets the data fields in the import data file.
	 * 
	 * @param dataFields the data fields in the import data file
	 */
	public void setDataFields(final List<?> dataFields) {
		this.dataFields = dataFields;
	}

	/**
	 * Returns the required fields for the import data type.
	 * 
	 * @return the required fields for the import data type
	 */
	public List<?> getRequiredFields() {
		return requiredFields;
	}

	/**
	 * Sets the required fields for the import data type.
	 * 
	 * @param requiredFields the required fields for the import data type
	 */
	public void setRequiredFields(final List<?> requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * Returns the optional fields for the import data type.
	 * 
	 * @return the optional fields for the import data type
	 */
	public List<?> getOptionalFields() {
		return optionalFields;
	}

	/**
	 * Sets the optional fields for the import data type.
	 * 
	 * @param optionalFields the optional fields for the import data type
	 */
	public void setOptionalFields(final List<?> optionalFields) {
		this.optionalFields = optionalFields;
	}

	/**
	 * Returns the mapping between data fields from data file and import data type fields.
	 * 
	 * @return the map between data fields from data file and import data type fields
	 */
	public Map<String, ?> getMappedFields() {
		return mappedFields;
	}

	/**
	 * Sets the mapping between data fields from data file and import data type fields.
	 * 
	 * @param mappedFields map between data fields from data file and import data type fields
	 */
	public void setMappedFields(final Map<String, ?> mappedFields) {
		this.mappedFields = mappedFields;
	}

	/**
	 * Returns a map with values set as integer type for those keys that have a value.
	 * 
	 * @return map with values set as integer type for those keys that have a value
	 */
	public Map<String, Integer> retrieveFinalMappedFields() {
		final Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (final Map.Entry<String, ?> entry : mappedFields.entrySet()) {
			final String fieldName = entry.getKey();

			// If the value of the entry is an <code>Integer</code> rather than a <code>String</code>,
			// that means the import mappings is obsolete. It might happen in the following case :
			// - A import job and mappings is created;
			// - Some attributes are added to the product type or category type linked with this import job.
			// We will just ignore the obsolete mappings.
			if (!(entry.getValue() instanceof String)) {
				continue;
			}

			final String col = (String) entry.getValue();
			if (col != null && col.length() > 0) {
				final Integer colNum = Integer.valueOf(col);
				resultMap.put(fieldName, colNum);
			}
		}
		return resultMap;
	}

	/**
	 * Sets the List of validation import errors.
	 * 
	 * @param validationResults List of of validation import errors
	 */
	public void setValidationResults(final List<?> validationResults) {
		this.validationResults = validationResults;
	}

	/**
	 * Returns the List of validation import errors.
	 * 
	 * @return the List of validation import errors
	 */
	public List<?> getValidationResults() {
		return this.validationResults;
	}

	/**
	 * Returns value of the warning confirmed field.
	 * 
	 * @return value of the warning confirmed field
	 */
	public boolean isWarningConfirmed() {
		return this.warningConfirmed;
	}

	/**
	 * Set value of the warning confirmed field.
	 * 
	 * @param warningConfirmed value of the warning confirmed field
	 */
	public void setWarningConfirmed(final boolean warningConfirmed) {
		this.warningConfirmed = warningConfirmed;
	}

	/**
	 * Sets the List of required fields validation.
	 * 
	 * @param requiredFieldsValidation the List of required fields validation
	 */
	public void setRequiredFieldsValidation(final List<?> requiredFieldsValidation) {
		this.requiredFieldsValidation = requiredFieldsValidation;
	}

	/**
	 * Returns the List of required fields validation.
	 * 
	 * @return the List of required fields validation
	 */
	public List<?> getRequiredFieldsValidation() {
		return requiredFieldsValidation;
	}

}
