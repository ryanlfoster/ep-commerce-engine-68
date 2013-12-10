package com.elasticpath.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.persistence.CsvFileReader;

/**
 * Test <code>CsvFileReader</code>.
 */
public class CsvFileReaderImplTest {

	private static final String COL = "col";

	private static final String ROW = "row";

	private static final String MAGIC_WORD = "_fran\u00e7ais_"; // \u00e7 is a special character.

	private CsvFileReader csvFileReader;

	/**
	 * Prepare for test.
	 */
	@Before
	public void setUp() {
		csvFileReader = new CsvFileReaderImpl() {
			@Override
			protected String getDatafileEncoding() {
				return "UTF-8";
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.persistence.impl.CsvFileReaderImpl.open(String)'.
	 * 
	 * @throws IOException in case of any IO error
	 */
	@Test
	public void testReadFile() throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String csvFileName = classLoader.getResource("csvFile1.csv").getFile();

		this.csvFileReader.open(csvFileName);
		String[] nextLine;
		int rowNumber = 0;
		while ((nextLine = this.csvFileReader.readNext()) != null) {

			int totalLength = 0;
			for (int colNumber = 1; colNumber < nextLine.length; colNumber++) {
				final String prefix = ROW + rowNumber;
				final String suffix = MAGIC_WORD + COL + colNumber;
				assertTrue(nextLine[colNumber].startsWith(prefix));
				assertTrue(nextLine[colNumber].endsWith(suffix));
				totalLength += nextLine[colNumber].length();
			}

			// [0] is the whole line
			assertTrue(nextLine[0].startsWith(ROW));
			assertTrue(nextLine[0].length() > totalLength);

			rowNumber++;
		}

		this.csvFileReader.close();
	}

	/**
	 * Test method for 'com.elasticpath.persistence.impl.CsvFileReaderImpl.open(String)'.
	 * 
	 * @throws IOException in case of any IO error
	 */
	@Test
	public void testReadFileWithInvalidDelimeterAndQualifier() throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String csvFileName = classLoader.getResource("csvFile1.csv").getFile();
		
		// invalid delimeter and qualifier will get ignored.
		this.csvFileReader.open(csvFileName, (char) 0, (char) 0);
		String[] nextLine;
		int rowNumber = 0;
		while ((nextLine = this.csvFileReader.readNext()) != null) {
			int totalLength = 0;
			for (int colNumber = 1; colNumber < nextLine.length; colNumber++) {
				final String prefix = ROW + rowNumber;
				final String suffix = COL + colNumber;
				assertTrue(nextLine[colNumber].startsWith(prefix));
				assertTrue(nextLine[colNumber].endsWith(suffix));
			}

			// [0] is the original line
			assertTrue(nextLine[0].startsWith(ROW));
			assertTrue(nextLine[0].length() > totalLength);

			rowNumber++;

		}

		this.csvFileReader.close();
	}

	/**
	 * Test method for 'com.elasticpath.persistence.impl.CsvFileReaderImpl.open(String, char, char)'.
	 * 
	 * @throws IOException in case of any IO error
	 */
	@Test
	public void testReadFileWithCustomizedDelimeterAndQualifier() throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String csvFileName = classLoader.getResource("csvFile2.csv").getFile();
		final char colDelimeter = '\t';
		final char textQualifier = '\'';
		this.csvFileReader.open(csvFileName, colDelimeter, textQualifier);
		String[] nextLine;
		int rowNumber = 0;
		while ((nextLine = this.csvFileReader.readNext()) != null) {
			int totalLength = 0;
			for (int colNumber = 1; colNumber < nextLine.length; colNumber++) {
				final String prefix = ROW + rowNumber;
				final String suffix = COL + colNumber;
				assertTrue(nextLine[colNumber].startsWith(prefix));
				assertTrue(nextLine[colNumber].endsWith(suffix));
			}

			// [0] is the original line
			assertTrue(nextLine[0].startsWith(ROW));
			assertTrue(nextLine[0].length() > totalLength);

			rowNumber++;
		}

		this.csvFileReader.close();
	}

	/**
	 * Test method for 'com.elasticpath.persistence.impl.CsvFileReaderImpl.getTopLines(String)'.
	 * 
	 * @throws IOException in case of any IO error
	 */
	@Test
	public void testReadTopLines() throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String csvFileName = classLoader.getResource("csvFile1.csv").getFile();

		// Try to read less lines than a file's total lines
		this.csvFileReader.open(csvFileName);
		List<String[]> previewData = this.csvFileReader.getTopLines(2);
		assertEquals(2, previewData.size());
		this.csvFileReader.close();

		// Try to read more lines than a file's total lines
		this.csvFileReader.open(csvFileName);
		previewData = this.csvFileReader.getTopLines(Integer.MAX_VALUE);
		final int totalLinesInFile = 4;
		assertEquals(totalLinesInFile, previewData.size());
		this.csvFileReader.close();
	}
}
