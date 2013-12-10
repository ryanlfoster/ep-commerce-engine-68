/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.CsvImportFieldObjectMapper;
import com.elasticpath.csvimport.CsvReadResult;
import com.elasticpath.csvimport.CsvReaderConfiguration;
import com.elasticpath.csvimport.DtoCsvLineReader;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Reads in DTOs from an input stream given CSV parsing criteria. This implementation
 * uses {@link CsvFileReader} to parse the input stream and a {@link CsvImportFieldObjectMapper} to
 * map the rows to Objects.
 * This implementation is stateful.
 * If 100 bad rows are reached during a read, the read will be aborted.
 * @param <T> the type of object being read
 */
public class DtoCsvLineReaderImpl<T> implements DtoCsvLineReader<T> {

	private static final Logger LOG = Logger.getLogger(DtoCsvLineReaderImpl.class);
	private BeanFactory beanFactory;
	private InputStream inputStream;
	private CsvReaderConfiguration csvReaderConfiguration;
	private CsvFileReader reader;
	private int position = 0;
	private boolean inputStreamFinished = false;
	private static final int MAX_BAD_ROWS = 100;
	private CsvImportFieldObjectMapper<T> mapper;
	
	/**
	 * Reads in the given number of DTOs from the current import stream, validating
	 * the data for each DTO at the same time. The reading starts from the current
	 * position within the import stream. If there are no more rows to import then
	 * {@link #isInputStreamFinished()} will return true. 
	 *  
	 * @param numRows the number of rows to read from the import stream
	 * @param retainObjects whether to retain the DTOs that are created during the read. If
	 * the goal is simply to validate the data in the input stream then retaining the created 
	 * objects might be an unnecessary use of memory. If false the objects will be created 
	 * and then discarded, relying on the DTO creation code to also perform validation.
	 * @return the result of the read process
	 */
	public CsvReadResult<T> readDtos(final int numRows, final boolean retainObjects) {
		sanityCheck();
		String[] row = null;
		int rowNumber = 0;
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
		List<ImportValidRow<T>> validRows = new ArrayList<ImportValidRow<T>>();
		while ((numRows == -1 || rowNumber < numRows)) {
			row = reader.readNext();
			if (row == null) {
				inputStreamFinished = true;
				break;
			}
			rowNumber++;

			List<ImportFault> faults = new ArrayList<ImportFault>();
			T dto = createDtoFromRow(row, faults);
			if (faults.isEmpty() && retainObjects) {
				validRows.add(createImportValidRow(dto, row[0], position)); 
			}
			if (!faults.isEmpty()) {
				badRows.add(
						createImportBadRow(position, StringUtils.defaultString(row[0]), faults));
			}
			if (badRows.size() > MAX_BAD_ROWS) {
				LOG.error("Maximum number of bad rows reached - aborting.");
				break;
			}
			position++;
		}
		return createReadResult(rowNumber, badRows, validRows);
	}
	
	/**
	 * Creates a CsvReadResult. Calls {@link #getReadResultBean()).
	 * @param totalRows the total number of rows
	 * @param badRows the bad rows
	 * @param validRows the valid rows
	 * @return the read result
	 */
	CsvReadResult<T> createReadResult(final int totalRows, 
			final Collection<ImportBadRow> badRows, final Collection<ImportValidRow<T>> validRows) {
		CsvReadResult<T> readResult = getReadResultBean();
		for (ImportBadRow badRow : badRows) {
			readResult.addBadRow(badRow);
		}
		for (ImportValidRow<T> validRow : validRows) {
			readResult.addValidRow(validRow);
		}
		readResult.setTotalRows(totalRows);
		return readResult;
	}

	/**
	 * Checks that the {@code CsvFileReader} has an open input stream and the {@code CsvReaderConfiguration} have been set.
	 */
	void sanityCheck() {
		if (getCsvFileReader() == null) {
			throw new IllegalStateException("reader not open");
		}
		if (getConfiguration() == null) {
			throw new IllegalStateException("configuration not set");
		}
	}
	
	/**
	 * Creates a new ImportValidRow.
	 * @param dto the dto that was assembled from the CSV.
	 * @param row the raw CSV string that was parsed to create the DTO
	 * @param rowNumber the row position within the file
	 * @return the valid row
	 */
	ImportValidRow<T> createImportValidRow(final T dto, final String row, final int rowNumber) {
		ImportValidRow<T> validRow = getBeanFactory().getBean(ContextIdNames.IMPORT_VALID_ROW);
		validRow.setDto(dto);
		validRow.setRow(row);
		validRow.setRowNumber(rowNumber);
		return validRow;
	}

	/**
	 * @return a new CsvReadResult implementation
	 */
	CsvReadResult<T> getReadResultBean() {
		return getBeanFactory().getBean(ContextIdNames.CSV_READ_RESULT);
	}
	
	/**
	 * Opens the reader's input stream.
	 * Calls {@link #getDelimiter()}, {@link #getTextQualifier()}, {@link #getEncoding()},
	 * {@link #getCsvFileReader()}.
	 * @throws IllegalStateException if the input stream has not been set
	 * @throws EpServiceException if there is a problem opening the InputStream 
	 */
	public void open() {
		if (getInputStream() == null) {
			throw new IllegalStateException("InputStream not set");
		}
		this.position = 0;
		this.reader = getCsvFileReader();
		try {
			reader.open(getInputStream(), getDelimiter(), getTextQualifier(), getEncoding());
		} catch (Exception ex) {
			throw new EpServiceException("There was a problem opening the InputStream.", ex);
		}
	}
	
	/**
	 * Calls {@link #getConfiguration()}.
	 * @return the CSV column delimiter
	 */
	char getDelimiter() {
		return getConfiguration().getDelimiter();
	}
	
	/**
	 * Calls {@link #getConfiguration()}.
	 * @return the CSV text qualifier
	 */
	char getTextQualifier() {
		return getConfiguration().getTextQualifier();
	}
	
	/**
	 * Calls {@link #getConfiguration()}.
	 * @return the input stream encoding
	 */
	String getEncoding() {
		return getConfiguration().getEncoding();
	}
	
	/**
	 * Gets a new CsvFileReader implementation.
	 * @return a new CsvFileReader
	 */
	CsvFileReader getCsvFileReader() {
		return getBeanFactory().getBean(ContextIdNames.CSV_FILE_READER);
	}
	
	/**
	 * Calls {@link #getConfiguration()}.
	 * @return the map of field names to column indexes
	 */
	Map<String, Integer> getFieldColumnIndexMapping() {
		return getConfiguration().getFieldColumnIndexMapping();
	}
	
	/**
	 * Closes the reader's input stream and resets {@link #isInputStreamFinished()} to false.
	 */
	public void close() {
		if (reader != null) {
			try {
				this.reader.close();
			} catch (Exception ex) {
				LOG.warn("Exception closing input stream.", ex);
			} finally {
				this.inputStreamFinished = false; //reset
			}
		}
	}
	
	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * @param inputStream the inputStream to set
	 */
	public void setInputStream(final InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * @return the csv reader configuration data.
	 */
	public CsvReaderConfiguration getConfiguration() {
		return csvReaderConfiguration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(final CsvReaderConfiguration configuration) {
		this.csvReaderConfiguration = configuration;
	}
	
	/**
	 * Creates a DTO from the given row of input strings.
	 * @param row the row of input strings
	 * @param faults list of import faults to populate with field-level import problems
	 * @return the created DTO
	 */
	T createDtoFromRow(final String[] row, final List<ImportFault> faults) {
		return getMapper().mapRow(row, getFieldColumnIndexMapping(), faults);
	}	
	
	/**
	 * Creates an {@code ImportBadRow} with the given data. For legacy reasons, we need to record not the actual position within
	 * the input stream, but the relative position within the reading window.
	 * @param rowNumber the row number within the reading window (e.g. The fifth row between rows 20 and 30 is actually row 5)
	 * @param firstString a string that will help the user to identify the bad row; usually the first string in a delimited row
	 * @param faults the field-level import faults that mark the row as bad
	 * @return the created ImportBadRow
	 */
	ImportBadRow createImportBadRow(
			final int rowNumber, final String firstString, final List<ImportFault> faults) {
		final ImportBadRow badRow = getBeanFactory().getBean(ContextIdNames.IMPORT_BAD_ROW);
		badRow.setRowNumber(rowNumber);
		badRow.setRow(firstString);
		badRow.addImportFaults(faults);
		return badRow;
	}
	
	/**
	 * Gets the object that will map String array representations of CSV rows to an object.
	 * @return the mapper
	 */
	public CsvImportFieldObjectMapper<T> getMapper() {
		return mapper;
	}

	/**
	 * Sets the object that will map String array representations of CSV rows to an object. 
	 * @param mapper the mapper to set
	 */
	public void setMapper(final CsvImportFieldObjectMapper<T> mapper) {
		this.mapper = mapper;
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * @return true if the input stream is at an end (the end of the file / stream
	 * has been reached)
	 */
	public boolean isInputStreamFinished() {
		return inputStreamFinished;
	}

	/**
	 * @return the reader
	 */
	protected CsvFileReader getReader() {
		return reader;
	}

	/**
	 * Increment position value.
	 */
	protected void incrementPosition() {
		this.position++;
	}

	/**
	 * @return the position
	 */
	protected int getPosition() {
		return position;
	}
}
