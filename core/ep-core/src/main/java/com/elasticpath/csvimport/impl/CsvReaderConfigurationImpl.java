/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.csvimport.CsvReaderConfiguration;

/**
 * Represents the criteria that a CSV reader might need to properly
 * parse a CSV file.
 */
public class CsvReaderConfigurationImpl implements CsvReaderConfiguration {

	private char delimiter = ',';
	private char textQualifier = '"';
	private String encoding = "UTF-8";
	private Map<String, Integer> mapping = new HashMap<String, Integer>();
		
	/**
	 * @return the delimiter character (defaults to comma)
	 */
	public char getDelimiter() {
		return this.delimiter;
	}

	/**
	 * @return the text qualifier, used in cases where a delimiter character
	 * might appear inside a string that should be imported. Defaults
	 * to double quote.
	 */
	public char getTextQualifier() {
		return this.textQualifier;
	}

	/**
	 * @param delimiter the CSV delimiter
	 */
	public void setDelimiter(final char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @param qualifier the text qualifier
	 */
	public void setTextQualifier(final char qualifier) {
		this.textQualifier = qualifier;
	}

	/**
	 * @return the CSV input stream's encoding (defaults to UTF-8)
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * @param encoding the CSV input stream's encoding (defaults to UTF-8)
	 */
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Gets the map of object field names to CSV column indexes.
	 * @return the map of field names to column indexes
	 */
	public Map<String, Integer> getFieldColumnIndexMapping() {
		return this.mapping;
	}

	/**
	 * Set the map of object field names to CSV column indexes.
	 * @param mapping the field name to column index map
	 */
	public void setFieldColumnIndexMapping(final Map<String, Integer> mapping) {
		this.mapping = mapping;
	}

}
