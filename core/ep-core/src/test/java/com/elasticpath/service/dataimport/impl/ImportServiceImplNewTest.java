package com.elasticpath.service.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.dataimport.ImportService;

/**
 * New test for {@code ImportServiceImpl} which does not extend {@code ElasticPathTestCase}.
 */
public class ImportServiceImplNewTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Tests that validateTitle succeeds when there is a hyphen in a field for a base amount import.
	 */
	@Test
	public void testValidateTitleBaseAmount() {
		ImportServiceImpl service = new ImportServiceImpl();
		
		final ImportJobRequest importJobRequest = context.mock(ImportJobRequest.class);
		final ImportJob importJob = new ImportJobImpl();
		importJob.setImportDataTypeName("Base Amount");
		
		context.checking(new Expectations() { {
			allowing(importJobRequest).getImportJob(); will(returnValue(importJob));
		} });
		
		String[] titleRow = {"empty", "PL-1_USD"};
		List<ImportBadRow> importBadRows = new ArrayList<ImportBadRow>();	
		
		service.validateTitleLine(titleRow, importBadRows, importJobRequest);
		
		assertTrue("Expect no faults", importBadRows.isEmpty());
	}
	
	/**
	 * Test double for {@code ImportServiceImpl}.
	 */
	private class ImportServiceTestDouble extends ImportServiceImpl {
		@Override
		protected ImportFault getImportFaultError() {
			return new ImportFaultImpl();
		}
		@SuppressWarnings("unchecked")
		@Override
		protected <T> T getBean(final String beanName) {
			if ("importBadRow".equals(beanName)) {
				return (T) new ImportBadRowImpl();
			}
			return null;
		}
	};
	
	/**
	 * Tests that validateTitle fails when there is a hyphen the header for a non
	 * Base Amount import.
	 */
	@Test
	public void testValidateTitleNotBaseAmount() {
		ImportServiceImpl service = new ImportServiceTestDouble();
		
		final ImportJobRequest importJobRequest = context.mock(ImportJobRequest.class);
		final ImportJob importJob = new ImportJobImpl();
		importJob.setImportDataTypeName("Category");
		
		context.checking(new Expectations() { {
			allowing(importJobRequest).getImportJob(); will(returnValue(importJob));
		} });
		
		String[] titleRow = {"empty", "PL-1_USD"};
		List<ImportBadRow> importBadRows = new ArrayList<ImportBadRow>();	
		
		service.validateTitleLine(titleRow, importBadRows, importJobRequest);
		
		assertEquals("Expect the hyphen to fail", 1, importBadRows.size());
	}
	
	/**
	 * Tests that <code>ImportService</code> returns an empty list if an empty set of guids is passed in.
	 */
	@Test
	public void testFindByGuidsWithEmptyGuids() {
		PersistenceEngine mockPersistenceEngine = context.mock(PersistenceEngine.class);
		ImportService importService = new ImportServiceImpl();
		importService.setPersistenceEngine(mockPersistenceEngine);
		assertEquals(Collections.<ImportJob>emptyList(), importService.findByGuids(Collections.<String>emptySet()));
	}
	
	/**
	 * Tests that <code>ImportService</code> returns an empty list if null is passed in for guids.
	 */
	@Test
	public void testFindByGuidsWithNullGuids() {
		PersistenceEngine mockPersistenceEngine = context.mock(PersistenceEngine.class);
		ImportService importService = new ImportServiceImpl();
		importService.setPersistenceEngine(mockPersistenceEngine);
		assertEquals(Collections.<ImportJob>emptyList(), importService.findByGuids(null));
	}
	
	/**
	 * Tests that <code>ImportService</code> returns the list of one <code>ImportJob</code> found by the query.
	 */
	@Test
	public void testFindByGuidsWithOneGuid() {
	    final Set<String> guids = new HashSet<String>(Arrays.asList("guid1"));  
		final List<ImportJob> importJobsFromQuery = new ArrayList<ImportJob>();
		ImportJob importJob = new ImportJobImpl();
		importJobsFromQuery.add(importJob);
	    
		final PersistenceEngine mockPersistenceEngine = context.mock(PersistenceEngine.class);
		ImportService importService = new ImportServiceImpl();
		importService.setPersistenceEngine(mockPersistenceEngine);
		
		context.checking(new Expectations() { {
			oneOf(mockPersistenceEngine).retrieveByNamedQueryWithList("IMPORT_JOB_FIND_BY_GUIDS", "list", guids);
				will(returnValue(importJobsFromQuery));
		} });
	
		List<ImportJob> returnedImportJobs = importService.findByGuids(guids);
		assertSame("The import jobs returned from the service call should be the same as what's returned from the query.",
				importJobsFromQuery, returnedImportJobs); 
	}
	
}
