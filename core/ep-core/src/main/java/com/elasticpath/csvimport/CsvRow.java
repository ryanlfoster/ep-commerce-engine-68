/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport;

/**
 * Represents a row in a CSV file.
 */
public interface CsvRow {
	
	/**
	 * Returns the row number that caused error.
	 * 
	 * @return the row number that caused error
	 */
	int getRowNumber();

	/**
	 * Set the row number that caused error.
	 * 
	 * @param rowNumber the row number that caused error
	 */
	void setRowNumber(int rowNumber);

	/**
	 * Returns the row that caused error.
	 * 
	 * @return the row that caused error
	 */
	String getRow();

	/**
	 * Set the row.
	 * 
	 * @param row the row to set
	 */
	void setRow(String row);
}
