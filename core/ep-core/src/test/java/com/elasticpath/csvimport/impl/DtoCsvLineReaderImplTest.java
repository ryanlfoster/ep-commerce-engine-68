/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.csvimport.CsvReaderConfiguration;
import com.elasticpath.persistence.CsvFileReader;

/**
 * Tests for DtoCsvReaderImpl.
 */
public class DtoCsvLineReaderImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final CsvReaderConfiguration criteria = context.mock(CsvReaderConfiguration.class);
	private final CsvFileReader csvFileReader = context.mock(CsvFileReader.class);
	
	/**
	 * Perform setup tasks.
	 */
	@Before
	public void setUp() {
		context.checking(new Expectations() { {
			allowing(criteria).getEncoding(); will(returnValue("UTF-8"));
			allowing(criteria).getDelimiter(); will(returnValue(','));
			allowing(criteria).getTextQualifier(); will(returnValue('"'));
		} });
	}
	
	/**
	 * Test that if the CsvFileReader has not been opened
	 * then a new IllegalStateException will be thrown.
	 */
	@Test(expected = IllegalStateException.class)
	public void testNoCsvFileReaderThrowsException() {
		DtoCsvLineReaderImpl< ? > reader = new DtoCsvLineReaderImpl<Object>() {
			@Override
			CsvFileReader getCsvFileReader() {
				return null;
			}
			
			@Override
			public CsvReaderConfiguration getConfiguration() {
				return criteria;
			}
			
		};
		reader.readDtos(-1, false);
	}
	
	/**
	 * Test that if the Criteria has not been set then a new IllegalStateException will be thrown.
	 */
	@Test(expected = IllegalStateException.class)
	public void testNoCriteriaThrowsException() {
		DtoCsvLineReaderImpl< ? > reader = new DtoCsvLineReaderImpl<Object>() {
			@Override
			CsvFileReader getCsvFileReader() {
				return csvFileReader;
			}
			
			@Override
			public CsvReaderConfiguration getConfiguration() {
				return null;
			}
			
		};
		reader.readDtos(-1, false);
	}
}
