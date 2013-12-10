/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport;


/**
 * Represents a valid CSV import row. A valid row is one that
 * could be converted into a DTO.
 * @param <T> the type of object
 */
public interface ImportValidRow<T> extends CsvRow {

	/**
	 * @return the corresponding DTO for the row
	 */
	T getDto();
	
	/**
	 * Sets the DTO for the row.
	 * 
	 * @param dto the DTO object of the row
	 */
	void setDto(T dto);
	
}
