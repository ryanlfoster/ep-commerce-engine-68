/**
 * 
 */
package com.elasticpath.csvimport;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.dataimport.ImportFault;


/**
 * Maps CSV fields onto a system object for CSV imports.
 * @param <T> the type of object being that will be mapped to the given row
 */
public interface CsvImportFieldObjectMapper<T> {
	
	/**
	 * Maps the columns in the given row to the fields on the given object,
	 * using the given helper service if not null.
	 * @param row the row
	 * @param columnIndexFieldNameMap map of object field names to column indexes
	 * @param faults the collection of faults while setting the object's fields to the column index values
	 * @return the populated object
	 */
	T mapRow(String[] row, Map<String, Integer> columnIndexFieldNameMap, Collection<ImportFault> faults);
	
}
