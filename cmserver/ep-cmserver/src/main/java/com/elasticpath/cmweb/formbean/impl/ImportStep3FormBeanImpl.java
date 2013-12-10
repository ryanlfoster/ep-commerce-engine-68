package com.elasticpath.cmweb.formbean.impl;

import com.elasticpath.cmweb.formbean.ImportStep3FormBean;
import com.elasticpath.domain.dataimport.ImportDataType;

/**
 * This bean represents import form.
 */
public class ImportStep3FormBeanImpl implements ImportStep3FormBean {

	private int importType;

	private String columnDelimiter;

	private String textQualifier;

	private ImportDataType importDataType;

	/**
	 * Returns the import type.
	 * 
	 * @return the import type
	 */
	public int getImportType() {
		return importType;
	}

	/**
	 * Sets the import type.
	 * 
	 * @param importType the import type
	 */
	public void setImportType(final int importType) {
		this.importType = importType;
	}

	/**
	 * Returns the column delimiter.
	 * 
	 * @return the column delimiter
	 */
	public String getColumnDelimiter() {
		return columnDelimiter;
	}

	/**
	 * Sets the column delimiter.
	 * 
	 * @param columnDelimiter the column delimiter
	 */
	public void setColumnDelimiter(final String columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}

	/**
	 * Returns the text qualifier.
	 * 
	 * @return the text qualifier
	 */
	public String getTextQualifier() {
		return textQualifier;
	}

	/**
	 * Sets the text qualifier.
	 * 
	 * @param textQualifier the text qualifier
	 */
	public void setTextQualifier(final String textQualifier) {
		this.textQualifier = textQualifier;
	}

	/**
	 * Sets the import data type.
	 * 
	 * @param importDataType the import data type
	 */
	public void setImportDataType(final ImportDataType importDataType) {
		this.importDataType = importDataType;
	}

	/**
	 * Returns the import data type.
	 * 
	 * @return the import data type
	 */
	public ImportDataType getImportDataType() {
		return this.importDataType;
	}
}
