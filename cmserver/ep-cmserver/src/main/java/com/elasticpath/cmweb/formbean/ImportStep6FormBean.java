/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean;

import java.util.List;

/**
 * <code>ImportStep6FormBean</code> represents the command object for import form.
 */
public interface ImportStep6FormBean {

	/**
	 * Retrieve the maximum allowable errors.
	 * 
	 * @return the maximum allowable errors
	 */
	int getMaxAllowErrors();

	/**
	 * Sets the maximum allowable errors.
	 * 
	 * @param maxAllowErrors the maximum allowable errors
	 */
	void setMaxAllowErrors(final int maxAllowErrors);

	/**
	 * Indicator to save import job.
	 * 
	 * @return true if saving import job, false otherwise
	 */
	boolean isSaveImport();

	/**
	 * Sets the indicator to save import job.
	 * 
	 * @param saveImport the indicator to save import job
	 */
	void setSaveImport(final boolean saveImport);

	/**
	 * Retrieves the import job name.
	 * 
	 * @return the import job name
	 */
	String getImportName();

	/**
	 * Sets the import job name.
	 * 
	 * @param importName the import job name
	 */
	void setImportName(final String importName);

	/**
	 * Indicator to execute the import job.
	 * 
	 * @return true if executing import job, false otherwise
	 */
	boolean isExecute();

	/**
	 * Sets the indicator to execute the import job.
	 * 
	 * @param execute the indicator to execute the import job
	 */
	void setExecute(final boolean execute);

	/**
	 * Indicator to set whether saving the import job is successful.
	 * 
	 * @return true if save import is successful, false othewise
	 */
	boolean isSaveImportSuccess();

	/**
	 * Sets the indicator to determine whether saving the import job is successful.
	 * 
	 * @param saveImportSuccess the indicator to determine whether saving the import job is successful
	 */
	void setSaveImportSuccess(final boolean saveImportSuccess);

	/**
	 * Indicator to set whether coming from step 1.
	 * 
	 * @return true if coming from step 1, false otherwise
	 */
	boolean isFromStep1();

	/**
	 * Sets the indicator to determine whether coming from step 1.
	 * 
	 * @param fromStep1 the indicator to determine whether coming from step 1
	 */
	void setFromStep1(final boolean fromStep1);

	/**
	 * Indicator to set whether import job has warning.
	 * 
	 * @return true if has warning, false otherwise
	 */
	boolean isWarning();

	/**
	 * Sets the indicator to determine whether import job has warning.
	 * 
	 * @param warning the indicator to determine whether import job has warning
	 */
	void setWarning(final boolean warning);
	
	/**
	 * Sets the list of validation import errors.
	 * 
	 * @param validationResults list of of validation import errors
	 */
	void setValidationResults(final List<?> validationResults);
	
	/**
	 * Returns the list of validation import errors.
	 * 
	 * @return the list of validation import errors
	 */
	List<?> getValidationResults();
	

}