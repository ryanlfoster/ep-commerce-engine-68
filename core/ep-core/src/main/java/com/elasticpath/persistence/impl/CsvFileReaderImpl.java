/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.persistence.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.settings.SettingsReader;

/**
 * Represent a csv file reader and writer.
 */
public class CsvFileReaderImpl implements CsvFileReader {
	/**
	 * The default column delimiter.
	 */
	public static final char DEFAULT_COL_DELIMETER = ',';

	/**
	 * The default text qualifier.
	 */
	public static final char DEFAULT_TEXT_QUALIFIER = '"';

	private String csvFileName;

	private CSVReader csvReader;

	private char colDelimeter = DEFAULT_COL_DELIMETER;

	private char textQualifier = DEFAULT_TEXT_QUALIFIER;

	private SettingsReader settingsReader;

	private static final String SETTING_DATAFILE_ENCODING = "COMMERCE/SYSTEM/datafileEncoding";
	
	/**
	 * Open a csv file.
	 * Calls {@link #getDatafileEncoding()} to get the character set in which
	 * the data file is expected to be encoded.
	 * 
	 * @param csvFileName the csv file name
	 * @throws EpPersistenceException if the given file doesn't exist
	 */
	public void open(final String csvFileName) throws EpPersistenceException {
		this.csvFileName = csvFileName;
		try {
			final FileInputStream fis = new FileInputStream(this.csvFileName);
			final InputStreamReader isr = new InputStreamReader(fis, getDatafileEncoding());
			this.csvReader = new CSVReader(new BufferedReader(isr), colDelimeter, textQualifier);
		} catch (FileNotFoundException e) {
			throw new EpPersistenceException("File not found:" + csvFileName, e);
		} catch (UnsupportedEncodingException e) {
			throw new EpPersistenceException("Encoding not support.", e);
		}
	}
	
	/**
	 * Opens a CSV input stream with the given column delimiter, text qualifier, and encoding.
	 * @param csvInputStream the input stream
	 * @param columnDelimiter the column delimiter
	 * @param textQualifier the text qualifier
	 * @param encoding the input stream's character set name. If not specified, will call
	 * {@link #getDatafileEncoding()}.
	 * @throws EpPersistenceException if the encoding is not supported
	 */
	public void open(final InputStream csvInputStream, 
			final char columnDelimiter, final char textQualifier, final String encoding) {
		String characterSetEncoding;
		if (StringUtils.isBlank(encoding)) {
			characterSetEncoding = getDatafileEncoding();
		} else {
			characterSetEncoding = encoding;
		}
		try {
			final InputStreamReader isr = new InputStreamReader(csvInputStream, characterSetEncoding);
			this.csvReader = new CSVReader(new BufferedReader(isr), columnDelimiter, textQualifier);
		} catch (UnsupportedEncodingException e) {
			throw new EpPersistenceException("Encoding not supported.", e);
		}
	}
	
	/**
	 * Gets the character set in which the data file is expected to be encoded.
	 * This implementation retrieves the data file encoding from the Settings Reader. 
	 * @return the datafile encoding character set.
	 * @throws com.elasticpath.base.exception.EpServiceException if there is a problem retrieving the setting
	 */
	protected String getDatafileEncoding() {
		return getSettingsReader().getSettingValue(SETTING_DATAFILE_ENCODING).getValue();
	}

	/**
	 * Open a csv file with the given column delimeter and text qualifier. If either parameter
	 * is less than or equal to zero, then class defaults will be used.
	 * 
	 * @param csvFileName the csv file name
	 * @param colDelimeter the column delimeter
	 * @param textQualifier the text qualifer
	 * @throws EpPersistenceException if the given file doesn't exist
	 */
	public void open(final String csvFileName, final char colDelimeter, final char textQualifier) throws EpPersistenceException {
		if (colDelimeter > 0) {
			this.colDelimeter = colDelimeter;
		}
		if (textQualifier > 0) {
			this.textQualifier = textQualifier;
		}

		this.open(csvFileName);
	}

	/**
	 * Read the next line.
	 * This implementation, due to the third party library that is being used, will split the next line
	 * using the configured delimiter character and insert the line's strings in an array's columns
	 * 1 through n. The first column of the array (column index 0) will contain the entire row as a single
	 * string, including the delimiter characters.
	 * 
	 * @return the next line if it's available (plus the entire line string in the 0th index), 
	 * otherwise <code>null</code> at the end of the input stream
	 * @throws EpPersistenceException in case of any IO error happens
	 */
	public String[] readNext() throws EpPersistenceException {
		try {
			final String[] row = this.csvReader.readNext();
			if (row == null) {
				return null;
			}
			for (int i = 0; i <= row.length - 1; i++) {
				row[i] = row[i].trim();
			}
			return row;
		} catch (IOException e) {
			throw new EpPersistenceException("File read error:" + csvFileName, e);
		}
	}

	/**
	 * Close the csv file.
	 * 
	 * @throws EpPersistenceException in case of any IO error happens
	 */
	public void close() throws EpPersistenceException {
		try {
			this.csvReader.close();
		} catch (IOException e) {
			throw new EpPersistenceException("File close error:" + csvFileName, e);
		}
	}

	/**
	 * Read the top lines with the give number. Notice: the file read cursor is moved to the line right after lines read.
	 * 
	 * @param lines the number of lines to read
	 * @return a list contains top lines with the give number.
	 * @throws EpPersistenceException in case of any IO error
	 */
	public List<String[]> getTopLines(final int lines) throws EpPersistenceException {
		final List<String[]> result = new ArrayList<String[]>();
		String[] nextLine;
		int rowNumber = 0;
		while (rowNumber < lines) {
			nextLine = this.readNext();
			if (nextLine == null) {
				break;
			}

			result.add(nextLine);
			rowNumber++;
		}
		return result;
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	@Override
	public int getTotalRows() throws EpPersistenceException {
		int totalRows = 0;
		try {
			while (csvReader.readNext() != null) {
				totalRows++;
			}
		} catch (IOException exc) {
			throw new EpPersistenceException("Cannot read file", exc);
		}
		// exclude the title line
		return totalRows - 1;
	}

}
