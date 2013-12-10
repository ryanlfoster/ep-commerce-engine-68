/**
 * 
 */
package com.elasticpath.csvimport;

/**
 * An extension to the DtoCsvLineReader<T> to allow parse header 
 * and get additional information located in header columns names.
 * @param <T> DTO object type for one row
 * @param <HEADER> header object type
 */
public interface DtoCsvLineReaderWithHeaderExtension<T, HEADER> extends DtoCsvLineReader<T> {

	/**
	 * Parse header row and return header specific data as single object or any collection, as will be implemented.
	 * @return an object with header information
	 */
	CsvReadResult<HEADER> readHeader();
}
