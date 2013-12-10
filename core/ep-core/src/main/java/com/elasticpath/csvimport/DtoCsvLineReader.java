/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport;

import java.io.InputStream;

/**
 * Implementors can read in a collection of DTO objects from
 * an input stream.
 * @param <T> the type of DTO being read
 */
public interface DtoCsvLineReader<T> {

	/**
	 * Reads in DTOs from an input stream using the given criteria
	 * to correctly parse the CSV.
	 * 
	 * @param numRows the number of rows to read in
	 * @param retainObjects if true, will retain the created DTOs. 
	 * Otherwise they will be discarded after reading them in (validation may
	 * be performed upon marshaling though)
	 * @return the result of the import
	 */
	CsvReadResult<T> readDtos(final int numRows, final boolean retainObjects);
	
	/**
	 * Sets the input stream to be read.
	 * @param inputStream the input stream
	 */
	void setInputStream(InputStream inputStream);
	
	/**
	 * Sets the criteria to be used by the CSV reader for parsing the CSV input stream. 
	 * @param criteria the criteria
	 */
	void setConfiguration(final CsvReaderConfiguration criteria);
	
	/**
	 * Opens the input stream.
	 */
	void open();
	
	/**
	 * Closes the input stream.
	 */
	void close();
	
	/**
	 * @return true if the input stream is at an end (the end of the file / stream
	 * has been reached)
	 */
	boolean isInputStreamFinished();
}
