package com.elasticpath.cmweb.formbean.impl;

import com.elasticpath.cmweb.formbean.ImportStep2FormBean;

/**
 * This bean represents import form.
 */
public class ImportStep2FormBeanImpl implements ImportStep2FormBean {

	private String importDataType;
	private String csvFileName;
	private boolean overwriteFile;
	
	/**
	 * Returns the import data type.
	 * 
	 * @return the import data type
	 */
	public String getImportDataType() {
		return importDataType;
	}

	/**
	 * Sets the import data type.
	 * 
	 * @param importDataType the import data type
	 */
	public void setImportDataType(final String importDataType) {
		this.importDataType = importDataType;
	}

	/**
	 * Returns the filename with an absolute path.
	 * 
	 * @return the filename with an absolute path
	 */
	public String getCsvFileName() {
		return csvFileName;
	}

	/**
	 * Sets the filename with an absolute path.
	 * 
	 * @param csvFileName the filename with an absolute path
	 */
	public void setCsvFileName(final String csvFileName) {
		this.csvFileName = csvFileName;
	}
	
	/**
	 * Indicator to set whether to overwrite existing file.
	 * 
	 * @return true if overwrite file, false othewise
	 */
	public boolean isOverwriteFile() {
		return overwriteFile;
	}

	/**
	 * Sets the indicator to determine whether to overwrite existing file.
	 * 
	 * @param overwriteFile the indicator to determine whether to overwrite existing file
	 */
	public void setOverwriteFile(final boolean overwriteFile) {
		this.overwriteFile = overwriteFile;
	}

}
