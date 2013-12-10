/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportJob;

/**
 * Test cases for {@link ImportJobRequestImpl}.
 */
public class ImportJobRequestImplTest {

	private ImportJobRequestImpl request;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ImportJob importJob;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		request = new ImportJobRequestImpl("myRequestId");
		importJob = context.mock(ImportJob.class);
	}

	/**
	 * Tests that the column delimiter falls back to the import job when possible.
	 */
	@Test
	public void testGetImportSourceColDelimiter() {
		char delimiter = request.getImportSourceColDelimiter();
		assertEquals("By default the character has undefined value", 0, delimiter);
		
		context.checking(new Expectations() { {
			oneOf(importJob).getCsvFileColDelimeter();
			will(returnValue('*'));
		} });
		request.setImportJob(importJob);
		
		assertEquals('*', request.getImportSourceColDelimiter());
		
	}

	/**
	 * Tests that the text qualifier falls back to the import job when possible.
	 */
	@Test
	public void testGetImportSourceTextQualifier() {
		char testQ = request.getImportSourceTextQualifier();
		assertEquals("By default the character has undefined value", 0, testQ);
		
		context.checking(new Expectations() { {
			oneOf(importJob).getCsvFileTextQualifier();
			will(returnValue('~'));
		} });
		request.setImportJob(importJob);
		
		assertEquals('~', request.getImportSourceTextQualifier());
		
	}

}
