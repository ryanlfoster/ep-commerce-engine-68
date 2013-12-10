/**
 * 
 */
package com.elasticpath.csvimport.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.CsvImportFieldObjectMapper;
import com.elasticpath.csvimport.CsvReadResult;
import com.elasticpath.csvimport.DtoCsvLineReaderWithHeaderExtension;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;

/**
 * Common implementation of {@link DtoCsvLineReaderWithHeaderExtension}.
 * @param <T> DTO object parsed from row
 * @param <HEADER> DTO object parsed from header
 */
public class DtoCsvLineReaderWithHeaderExtensionImpl<T, HEADER> extends DtoCsvLineReaderImpl<T> 
				implements DtoCsvLineReaderWithHeaderExtension<T, HEADER> {

	private CsvImportFieldObjectMapper<HEADER> mapperForHeader;

	
	@Override
	public CsvReadResult<HEADER> readHeader() {

		String[] row = null;
		int rowNumber = 0;
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
		List<ImportValidRow<HEADER>> validRows = new ArrayList<ImportValidRow<HEADER>>();

		row = getReader().readNext();
		if (row == null) {
			return null;
		}

		List<ImportFault> faults = new ArrayList<ImportFault>();
		
		HEADER dto = mapperForHeader.mapRow(row, getFieldColumnIndexMapping(), faults);
		
		if (faults.isEmpty()) {
			validRows.add(createImportValidRowForHeader(dto, row[0], this.getPosition())); 
		}
		if (!faults.isEmpty()) {
			badRows.add(
					createImportBadRow(this.getPosition(), StringUtils.defaultString(row[0]), faults));
		}
		this.incrementPosition();
		
		return createReadResultForHeader(rowNumber, badRows, validRows);
	}
	
	/**
	 * Creates a new ImportValidRow.
	 * @param dto the dto that was assembled from the CSV.
	 * @param row the raw CSV string that was parsed to create the DTO
	 * @param rowNumber the row position within the file
	 * @return the valid row
	 */
	private ImportValidRow<HEADER> createImportValidRowForHeader(final HEADER dto, final String row, final int rowNumber) {
		ImportValidRow<HEADER> validRow = getBeanFactory().getBean(ContextIdNames.IMPORT_VALID_ROW);
		validRow.setDto(dto);
		validRow.setRow(row);
		validRow.setRowNumber(rowNumber);
		return validRow;
	}

	
	/**
	 * Creates a CsvReadResult. Calls {@link #getReadResultBean()).
	 * @param totalRows the total number of rows
	 * @param badRows the bad rows
	 * @param validRows the valid rows
	 * @return the read result
	 */
	private CsvReadResult<HEADER> createReadResultForHeader(final int totalRows, 
			final Collection<ImportBadRow> badRows, final Collection<ImportValidRow<HEADER>> validRows) {
		
		CsvReadResult<HEADER> readResult = getBeanFactory().getBean(ContextIdNames.CSV_READ_RESULT);
		for (ImportBadRow badRow : badRows) {
			readResult.addBadRow(badRow);
		}
		for (ImportValidRow<HEADER> validRow : validRows) {
			readResult.addValidRow(validRow);
		}
		readResult.setTotalRows(totalRows);
		return readResult;
	}

	/**
	 * @param mapperForHeader the mapperForHeader to set
	 */
	public void setMapperForHeader(final CsvImportFieldObjectMapper<HEADER> mapperForHeader) {
		this.mapperForHeader = mapperForHeader;
	}

}
