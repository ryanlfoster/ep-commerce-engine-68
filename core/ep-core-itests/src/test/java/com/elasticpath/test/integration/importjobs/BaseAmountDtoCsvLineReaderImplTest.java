/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.csvimport.CsvReadResult;
import com.elasticpath.csvimport.CsvReaderConfiguration;
import com.elasticpath.csvimport.DtoCsvLineReader;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.pricing.csvimport.impl.ImportDataTypeBaseAmountImpl;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.ImportJobScenario;


/**
 * Test coverage for {@code BaseAmountDtoCsvLineReaderImpl}.
 */
public class BaseAmountDtoCsvLineReaderImplTest extends ImportJobTestCase {
	private static final int CHUNK_SIZE = 10;
	private static final int NUMBER_OF_RECORD = 6;	
	private static final int NUMBER_OF_INVALID_RECORD = 3;	
	private static final String VALID_FILE = "/import/baseamount.csv";
	private static final String INVALID_FILE = "/import/baseamount-invalid.csv";

	@Autowired
	@Qualifier("baseAmountDtoCsvLineReader")
	private DtoCsvLineReader<?> dtoCsvLineReader;

	@Autowired
	@Qualifier("csvReaderConfiguration")
	private CsvReaderConfiguration criteria;

	private InputStream validInputStream;
	private InputStream invalidInputStream;
	
	/**
	 * Basic setup.
	 * @throws Exception Might be IO exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(ImportJobScenario.class);
		this.setMappings();
		
		criteria.setDelimiter(ImportJobTestCase.CSV_FILE_COL_DELIMETER);
		criteria.setTextQualifier(ImportJobTestCase.CSV_FILE_TEXT_QUALIFIER);
		this.dtoCsvLineReader.setConfiguration(criteria);
		
		String assetRootFolder = new File(".").getCanonicalPath() + File.separator + "integrationtests" + File.separator + "assets";
		
		this.validInputStream = new FileInputStream(new File(assetRootFolder + BaseAmountDtoCsvLineReaderImplTest.VALID_FILE));
		this.invalidInputStream = new FileInputStream(new File(assetRootFolder + BaseAmountDtoCsvLineReaderImplTest.INVALID_FILE));
	}
	
	/**
	 * Test for {@link BaseAmountDtoCsvLineReaderImpl#readDtos(int, boolean)}.
	 * Assumes all entries in the file are valid.
	 * This test will expect 6 valid rows as after reading data from file.
	 * Uses 'baseamount.csv' for successful entries - contains 6 valid entries
	 */
	@Ignore("Needs to be investigated")
	@DirtiesDatabase
	@Test
	public void testReadDtosForSuccessfulEntries() {
		this.dtoCsvLineReader.setInputStream(validInputStream);
		this.dtoCsvLineReader.open();
		
		// read headers
		this.dtoCsvLineReader.readDtos(1, false); 
		List<ImportValidRow<?>> validRows = new ArrayList<ImportValidRow<?>>();
		int totalRows = -1; // skipping header line
		
		while (!this.dtoCsvLineReader.isInputStreamFinished()) {
			CsvReadResult<?> result = this.dtoCsvLineReader.readDtos(BaseAmountDtoCsvLineReaderImplTest.CHUNK_SIZE, true);
			validRows.addAll(result.getValidRows());
			totalRows += result.getTotalRows();
		}
		
		this.dtoCsvLineReader.close();
		assertEquals(BaseAmountDtoCsvLineReaderImplTest.NUMBER_OF_RECORD, validRows.size());
		assertEquals(BaseAmountDtoCsvLineReaderImplTest.NUMBER_OF_RECORD, totalRows);
	}
	
	/**
	 * Test for {@link BaseAmountDtoCsvLineReaderImpl#readDtos(int, boolean)}.
	 * Assumes some entries in the file are invalid.
	 * This test will expect 3 invalid rows after reading data from file.
	 * Uses 'baseamount-invalid.csv' for invalid entries - contains 3 valid and 3 invalid entries
	 */
	@Ignore("Needs to be investigated")
	@DirtiesDatabase
	@Test
	public void testReadDtosForInvalidEntries() {
		this.dtoCsvLineReader.setInputStream(invalidInputStream);
		this.dtoCsvLineReader.open();
		
		// read headers
		this.dtoCsvLineReader.readDtos(1, false); 
		List<ImportBadRow> invalidRows = new ArrayList<ImportBadRow>();
		int totalRows = -1; // skipping header line

		while (!this.dtoCsvLineReader.isInputStreamFinished()) {
			CsvReadResult<?> result = this.dtoCsvLineReader.readDtos(BaseAmountDtoCsvLineReaderImplTest.CHUNK_SIZE, true);
			invalidRows.addAll(result.getBadRows());
			totalRows += result.getTotalRows();
		}
		
		this.dtoCsvLineReader.close();
		assertEquals(BaseAmountDtoCsvLineReaderImplTest.NUMBER_OF_INVALID_RECORD, invalidRows.size());
		assertEquals(BaseAmountDtoCsvLineReaderImplTest.NUMBER_OF_RECORD, totalRows);
	}

	private void setMappings() {
		ImportDataType dataType = new ImportDataTypeBaseAmountImpl();
		dataType.init(null);
		
		int index = 0;
		for (ImportField field : dataType.getImportFields().values()) {
			criteria.getFieldColumnIndexMapping().put(field.getName(), index++);					
		}
	}
}
