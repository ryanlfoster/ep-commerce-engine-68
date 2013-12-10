/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.csvimport.CsvReadResult;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;

/**
 * Container for the results of reading some piece of a CSV InputStream.
 * @param <T> the type of object in the ImportValidRow objects (e.g. BaseAmountDTO)
 */
public class CsvReadResultImpl<T> implements CsvReadResult<T> {

	private int totalRows = 0;
	private final List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
	private final List<ImportValidRow<T>> validRows = new ArrayList<ImportValidRow<T>>();
	
	/**
	 * @return the total number of rows that were read.
	 */
	public int getTotalRows() {
		return totalRows;
	}
	
	/**
	 * @param totalRows the total number of rows that were read
	 */
	public void setTotalRows(final int totalRows) {
		this.totalRows = totalRows;
	}
	
	/**
	 * @return the bad rows that could not be read
	 */
	public List<ImportBadRow> getBadRows() {
		return badRows;
	}
	
	/**
	 * Adds a bad row.
	 * @param badRow the bad row to add
	 */
	public void addBadRow(final ImportBadRow badRow) {
		this.badRows.add(badRow);
	}

	/**
	 * Add a successfully read csv row and its corresponding successfully assembled DTO.
	 * @param validRow the csv row that was read
	 */
	public void addValidRow(final ImportValidRow<T> validRow) {
		this.validRows.add(validRow);
	}

	/**
	 * Gets the valid rows (the rows that were read in from CSV and converted to DTOs).
	 * @return the valid rows
	 */
	public List<ImportValidRow<T>> getValidRows() {
		return this.validRows;
	}
}
