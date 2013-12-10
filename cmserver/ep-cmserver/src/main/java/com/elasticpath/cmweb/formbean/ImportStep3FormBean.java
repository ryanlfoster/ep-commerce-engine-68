/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean;

import com.elasticpath.domain.dataimport.ImportDataType;


/**
 * <code>ImportStep3FormBean</code> represents the command object for import form.
 */
public interface ImportStep3FormBean {

	/**
	 * Returns the import type.
	 * 
	 * @return the import type
	 */
	int getImportType();

	/**
	 * Sets the import type.
	 * 
	 * @param importType the import type
	 */
	void setImportType(final int importType);
	
	/**
	 * Returns the column delimiter.
	 * 
	 * @return the column delimiter
	 */
	String getColumnDelimiter();

	/**
	 * Sets the column delimiter.
	 * 
	 * @param columnDelimiter the column delimiter
	 */
	void setColumnDelimiter(final String columnDelimiter);

	/**
	 * Returns the text qualifier.
	 * 
	 * @return the text qualifier
	 */
	String getTextQualifier();

	/**
	 * Sets the text qualifier.
	 * 
	 * @param textQualifier the text qualifier
	 */
	void setTextQualifier(final String textQualifier);

	/**
	 * Sets the import data type.
	 * @param importDataType the import data type
	 */
	void setImportDataType(final ImportDataType importDataType);
	
	/**
	 * Returns the import data type.
	 * @return the import data type
	 */
	ImportDataType getImportDataType();
}
