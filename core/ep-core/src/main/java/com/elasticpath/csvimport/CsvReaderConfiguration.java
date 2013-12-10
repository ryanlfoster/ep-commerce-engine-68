/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport;

import java.util.Map;


/**
 * Contains configuration data of interest to a CSV reader, to help it process a CSV input stream
 * and convert the rows into java objects.
 */
public interface CsvReaderConfiguration {

	/**
	 * Sets the CSV delimiter.
	 * @param delimiter the delimiter to set
	 */
	void setDelimiter(char delimiter);
	
	/**
	 * @return the CSV delimiter
	 */
	char getDelimiter();
	
	/**
	 * Sets the CSV text qualifier (the character surrounding chars that should be treated as text,
	 * in the case that delimiter characters are to be imported).
	 * @param qualifier the qualifier to set
	 */
	void setTextQualifier(char qualifier);
	
	/**
	 * @return the CSV text qualifier (the character surrounding chars that should be treated as text,
	 * in the case that delimiter characters are to be imported).
	 */
	char getTextQualifier();
	
	/**
	 * @return the CSV file encoding. 
	 */
	String getEncoding();
	
	/**
	 * Sets the CSV file encoding.
	 * @param encoding the encoding to set
	 */
	void setEncoding(String encoding);
	
	/**
	 * Gets the map of object field names to CSV column indexes.
	 * @return the map of field names to column indexes
	 */
	Map<String, Integer> getFieldColumnIndexMapping();
	
	/**
	 * Set the map of object field names to CSV column indexes.
	 * @param mapping the field name to column index map
	 */
	void setFieldColumnIndexMapping(Map<String, Integer> mapping);
}
