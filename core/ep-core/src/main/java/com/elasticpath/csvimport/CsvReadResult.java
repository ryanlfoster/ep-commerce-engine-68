/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport;

import java.util.List;

import com.elasticpath.domain.dataimport.ImportBadRow;

/**
 * Contains information relating to the results of a CSV read operation.
 * @param <T> The type of DTO in the Valid Rows (e.g. BaseAmountDTO)
 */
public interface CsvReadResult<T> {

	/**
	 * @return the total number of rows read
	 */
	int getTotalRows();

	/**
	 * @param totalRows the total number of rows read
	 */
	void setTotalRows(int totalRows);

	/**
	 * @return the badRows
	 */
	List<ImportBadRow> getBadRows();
	
	/**
	 * @return list of the valid csv rows that were read to the DTOs that were successfully assembled from the rows.
	 */
	List<ImportValidRow<T>> getValidRows();
	
	/**
	 * Add a valid read csv row and its corresponding successfully assembled DTO.
	 * @param validRow the csv row that was read
	 */
	void addValidRow(ImportValidRow<T> validRow);
	
	/**
	 * Add an invalid csv row.
	 *  
	 * @param badRow the csv row that was read
	 */
	void addBadRow(ImportBadRow badRow);
}