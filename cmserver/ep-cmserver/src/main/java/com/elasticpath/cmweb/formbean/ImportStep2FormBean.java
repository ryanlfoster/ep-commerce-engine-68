/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean;

/**
 * <code>ImportStep2FormBean</code> represents the command object for import form.
 */
public interface ImportStep2FormBean {

	/**
	 * Returns the import data type.
	 * 
	 * @return the import data type
	 */
	String getImportDataType();

	/**
	 * Sets the import data type.
	 * 
	 * @param importDataType the import data type
	 */
	void setImportDataType(final String importDataType);
	
	/**
	 * Returns the filename with an absolute path.
	 * 
	 * @return the filename with an absolute path
	 */
	String getCsvFileName();
	
	/**
	 * Sets the filename with an absolute path.
	 * 
	 * @param csvFileName the filename with an absolute path
	 */
	void setCsvFileName(final String csvFileName);
	
	/**
	 * Indicator to set whether to overwrite existing file.
	 * 
	 * @return true if overwrite file, false othewise
	 */
	boolean isOverwriteFile();

	/**
	 * Sets the indicator to determine whether to overwrite existing file.
	 * 
	 * @param overwriteFile the indicator to determine whether to overwrite existing file
	 */
	void setOverwriteFile(final boolean overwriteFile);

}