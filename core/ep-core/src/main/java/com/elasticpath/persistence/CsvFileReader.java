/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.persistence;

import java.io.InputStream;
import java.util.List;

import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Represent a csv file reader and writer.
 */
public interface CsvFileReader {

	/**
	 * Open a csv file.
	 * 
	 * @param csvFileName the csv file name
	 * @throws EpPersistenceException if the given file doesn't exist
	 */
	void open(String csvFileName) throws EpPersistenceException;

	/**
	 * Open a csv file with the given column delimeter and text qualifier.
	 * 
	 * @param csvFileName the csv file name
	 * @param colDelimeter the column delimeter
	 * @param textQualifier the text qualifer
	 * @throws EpPersistenceException if the given file doesn't exist
	 */
	void open(String csvFileName, char colDelimeter, char textQualifier) throws EpPersistenceException;
	
	/**
	 * Open a CSV input stream with the given column delimiter and text qualifier.
	 * @param csvInputStream the input stream
	 * @param colDelimiter the column delimiter
	 * @param encoding the input stream's character set name (e.g. "UTF-8")
	 * @param textQualifier the text qualifier
	 */
	void open(InputStream csvInputStream, char colDelimiter, char textQualifier, String encoding);

	/**
	 * Read the next line.
	 * 
	 * @return the next line if it's available, otherwise <code>null</code> at the end of file
	 * @throws EpPersistenceException in case of any IO error happens
	 */
	String[] readNext() throws EpPersistenceException;

	/**
	 * Close the csv file.
	 * 
	 * @throws EpPersistenceException in case of any IO error happens
	 */
	void close() throws EpPersistenceException;

	/**
	 * Read the top lines with the give number. Notice: the file read cursor is moved to the line right after lines read.
	 * 
	 * @param lines the number of lines to read
	 * @return a list contains top lines with the give number.
	 * @throws EpPersistenceException in case of error
	 */
	List<String[]> getTopLines(int lines) throws EpPersistenceException;

	/**
	 * Gets the total number of rows for the open file.
	 * 
	 * @return the total number of rows
	 */
	int getTotalRows();

}
