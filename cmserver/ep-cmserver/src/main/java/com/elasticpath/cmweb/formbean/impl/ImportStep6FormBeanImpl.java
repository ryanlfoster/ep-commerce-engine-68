package com.elasticpath.cmweb.formbean.impl;

import java.util.List;

import com.elasticpath.cmweb.formbean.ImportStep6FormBean;

/**
 * This bean represents import form.
 */
public class ImportStep6FormBeanImpl implements ImportStep6FormBean {

	private int maxAllowErrors;

	private boolean saveImport;

	private boolean execute;

	private boolean saveImportSuccess;

	private boolean fromStep1;

	private boolean warning;

	private String importName;

	private List<?> validationResults;

	/**
	 * Retrieve the maximum allowable errors.
	 * 
	 * @return the maximum allowable errors
	 */
	public int getMaxAllowErrors() {
		return maxAllowErrors;
	}

	/**
	 * Sets the maximum allowable errors.
	 * 
	 * @param maxAllowErrors the maximum allowable errors
	 */
	public void setMaxAllowErrors(final int maxAllowErrors) {
		this.maxAllowErrors = maxAllowErrors;
	}

	/**
	 * Indicator to save import job.
	 * 
	 * @return true if saving import job, false otherwise
	 */
	public boolean isSaveImport() {
		return saveImport;
	}

	/**
	 * Sets the indicator to save import job.
	 * 
	 * @param saveImport the indicator to save import job
	 */
	public void setSaveImport(final boolean saveImport) {
		this.saveImport = saveImport;
	}

	/**
	 * Retrieves the import job name.
	 * 
	 * @return the import job name
	 */
	public String getImportName() {
		return importName;
	}

	/**
	 * Sets the import job name.
	 * 
	 * @param importName the import job name
	 */
	public void setImportName(final String importName) {
		this.importName = importName;
	}

	/**
	 * Indicator to execute the import job.
	 * 
	 * @return true if executing import job, false otherwise
	 */
	public boolean isExecute() {
		return execute;
	}

	/**
	 * Sets the indicator to execute the import job.
	 * 
	 * @param execute the indicator to execute the import job
	 */
	public void setExecute(final boolean execute) {
		this.execute = execute;
	}

	/**
	 * Indicator to set whether saving the import job is successful.
	 * 
	 * @return true if save import is successful, false othewise
	 */
	public boolean isSaveImportSuccess() {
		return saveImportSuccess;
	}

	/**
	 * Sets the indicator to determine whether saving the import job is successful.
	 * 
	 * @param saveImportSuccess the indicator to determine whether saving the import job is successful
	 */
	public void setSaveImportSuccess(final boolean saveImportSuccess) {
		this.saveImportSuccess = saveImportSuccess;
	}

	/**
	 * Indicator to set whether coming from step 1.
	 * 
	 * @return true if coming from step 1, false othewise
	 */
	public boolean isFromStep1() {
		return fromStep1;
	}

	/**
	 * Sets the indicator to determine whether coming from step 1.
	 * 
	 * @param fromStep1 the indicator to determine whether coming from step 1
	 */
	public void setFromStep1(final boolean fromStep1) {
		this.fromStep1 = fromStep1;
	}

	/**
	 * Indicator to set whether import job has warning.
	 * 
	 * @return true if has warning, false otherwise
	 */
	public boolean isWarning() {
		return warning;
	}

	/**
	 * Sets the indicator to determine whether import job has warning.
	 * 
	 * @param warning the indicator to determine whether import job has warning
	 */
	public void setWarning(final boolean warning) {
		this.warning = warning;
	}

	/**
	 * Sets the list of validation import errors.
	 * 
	 * @param validationResults list of of validation import errors
	 */
	public void setValidationResults(final List<?> validationResults) {
		this.validationResults = validationResults;
	}

	/**
	 * Returns the list of validation import errors.
	 * 
	 * @return the list of validation import errors
	 */
	public List<?> getValidationResults() {
		return this.validationResults;
	}
}
