/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import com.elasticpath.csvimport.CsvRow;

/**
 * Default implementation of CsvRow.
 */
public class CsvRowImpl implements CsvRow {
	
	private String row;
	private int rowNumber;
	
	/**
	 * @return the row
	 */
	public String getRow() {
		return row;
	}
	
	/**
	 * @param row the row to set
	 */
	public void setRow(final String row) {
		this.row = row;
	}
	
	/**
	 * @return the rowNumber
	 */
	public int getRowNumber() {
		return rowNumber;
	}
	
	/**
	 * @param rowNumber the rowNumber to set
	 */
	public void setRowNumber(final int rowNumber) {
		this.rowNumber = rowNumber;
	}
}
