/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportNotificationMetadata;

/**
 * Test cases for {@link ImportNotificationImpl}.
 */
public class ImportNotificationImplTest {

	private ImportNotificationImpl importNotification;
	private ImportJob importJob;
	private ImportNotificationMetadata importNotificationMetadata;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		importNotification = new ImportNotificationImpl() {
			private static final long serialVersionUID = -5863642401236807903L;

			/**
			 */
			@SuppressWarnings("unchecked")
			@Override
			protected <T> T getBean(final String beanName) {
				if ("importNotificationMetadata".equals(beanName)) {
					return (T) importNotificationMetadata;
				}
				throw new IllegalArgumentException("Unexpected bean name: " + beanName);
			}
		};
		importJob = context.mock(ImportJob.class); 
		importNotificationMetadata = context.mock(ImportNotificationMetadata.class);
	}

	/**
	 * Tests that getting max allowed failed rows returns proper values.
	 */
	@Test
	public void testGetMaxAllowedFailedRows() {
		final int valueOverride = 2;
		context.checking(new Expectations() { {
			oneOf(importNotificationMetadata).setKey(ImportNotificationImpl.KEY_MAX_ALLOWED_FAILED_ROWS);
			oneOf(importNotificationMetadata).setValue(String.valueOf(valueOverride));
			oneOf(importNotificationMetadata).getValue();
			will(returnValue(String.valueOf(valueOverride)));
			
			oneOf(importJob).getMaxAllowErrors();
			will(returnValue(1));
		} });
		
		importNotification.setImportJob(importJob);
		assertEquals("Having no value set, this should return 1", 1, importNotification.getMaxAllowedFailedRows());
		
		importNotification.setMaxAllowedFailedRows(valueOverride);
		assertEquals("Having value set to 2, this should override the import job value(1)", 
				valueOverride, importNotification.getMaxAllowedFailedRows());
	}

	/**
	 * Tests that getting column delimiter returns proper values.
	 */
	@Test
	public void testGetImportSourceColDelimiter() {
		final char valueOverride = '%';
		context.checking(new Expectations() { {
			oneOf(importNotificationMetadata).setKey(ImportNotificationImpl.KEY_COLUMN_DELIMITER);
			oneOf(importNotificationMetadata).setValue(String.valueOf(valueOverride));
			oneOf(importNotificationMetadata).getValue();
			will(returnValue(String.valueOf(valueOverride)));
			
			oneOf(importJob).getCsvFileColDelimeter();
			will(returnValue('*'));
		} });
		
		importNotification.setImportJob(importJob);
		assertEquals("Having no value set, this should return the import job value", '*', importNotification.getImportSourceColDelimiter());
		
		importNotification.setImportSourceColDelimiter(valueOverride);
		assertEquals("Having a value set, this should override the import job value", 
				valueOverride, importNotification.getImportSourceColDelimiter());
		
	}

	/**
	 * Tests that getting text qualifier returns proper values.
	 */
	@Test
	public void testGetImportSourceTextQualifier() {
		final char valueOverride = '#';
		final char importJobValue = ';';
		context.checking(new Expectations() { {
			oneOf(importNotificationMetadata).setKey(ImportNotificationImpl.KEY_TEXT_QUALIFIER);
			oneOf(importNotificationMetadata).setValue(String.valueOf(valueOverride));
			oneOf(importNotificationMetadata).getValue();
			will(returnValue(String.valueOf(valueOverride)));
			
			oneOf(importJob).getCsvFileTextQualifier();
			will(returnValue(importJobValue));
		} });
		
		importNotification.setImportJob(importJob);
		assertEquals("Having no value set, this should return the import job value", importJobValue, 
				importNotification.getImportSourceTextQualifier());
		
		importNotification.setImportSourceTextQualifier(valueOverride);
		assertEquals("The value set should override the import job value", 
				valueOverride, importNotification.getImportSourceTextQualifier());

	}

	/**
	 * Tests that toString() returns a non-null value.
	 */
	@Test
	public void testToString() {
		// the import job is a required field
		importNotification.setImportJob(importJob);

		context.checking(new Expectations() { {
			oneOf(importJob).getCsvFileName();
		} });

		assertNotNull("Should not be null", importNotification.toString());
		
	}

}
