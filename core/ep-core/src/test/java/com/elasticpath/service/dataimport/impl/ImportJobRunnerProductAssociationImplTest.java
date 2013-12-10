/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.service.dataimport.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;
import org.hamcrest.collection.IsArray;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for {@link ImportJobRunnerProductAssociationImpl}.
 */
public class ImportJobRunnerProductAssociationImplTest {

	private static final String SOURCE_PRODUCT_CODE = "10020228";
    private static final String IMPORT_PROCESS_ID = "id1";

    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

    private ImportJobRunnerProductAssociationImpl importJobRunnerImpl;
	private ImportJob importJob;
	private UtilityImpl utility;
	private ImportService importService;
	private ImportDataTypeProductAssociationImpl importDataType;
	private PersistenceSession persistenceSession;
	private Transaction transaction;
	private ImportJobStatusHandler jobStatusHandler;
	private CsvFileReader csvFileReader;
	private ChangeSetService changeSetService;
	private ElasticPath elasticPath;
	private PersistenceEngine persistenceEngine;
	private CmUser cmUser;
	
	/**
	 * Test initialization.
	 * 
	 */
	@Before
	public void setUp() {
			
		importDataType = new ImportDataTypeProductAssociationImpl();
		utility = new UtilityImpl();

		importService = context.mock(ImportService.class);
		csvFileReader = context.mock(CsvFileReader.class);
		elasticPath = context.mock(ElasticPath.class);
		changeSetService = context.mock(ChangeSetService.class);
		jobStatusHandler = context.mock(ImportJobStatusHandler.class);
		persistenceEngine = context.mock(PersistenceEngine.class);
		persistenceSession = context.mock(PersistenceSession.class);
		transaction = context.mock(Transaction.class);
		importJob = context.mock(ImportJob.class);
		cmUser = context.mock(CmUser.class);

		context.checking(new Expectations() {
			{
				allowing(elasticPath).getBean(ContextIdNames.PRODUCT_ASSOCIATION); will(returnValue(new ProductAssociationImpl()));
				allowing(elasticPath).getBean(ContextIdNames.IMPORT_BAD_ROW); will(returnValue(new ImportBadRowImpl()));
				allowing(elasticPath).getBean(ContextIdNames.IMPORT_FAULT); will(returnValue(new ImportFaultImpl()));
				
				allowing(importService).findImportDataType(with(any(String.class))); will(returnValue(importDataType));
				allowing(importService).initImportDataTypeLocalesAndCurrencies(importDataType, importJob);
				
				allowing(transaction).commit();
				allowing(persistenceSession).beginTransaction(); will(returnValue(transaction));
				allowing(persistenceSession).close();
				allowing(persistenceSession).save(with(any(Persistable.class)));
				allowing(persistenceEngine).getSharedPersistenceSession(); will(returnValue(persistenceSession));
				allowing(persistenceEngine).isCacheEnabled(); will(returnValue(false));

				allowing(importJob).getImportDataTypeName(); will(returnValue("Test Product Association Import Data Type"));
				allowing(importJob).getMappings(); will(returnValue(new HashMap<String, Integer>()));
				allowing(importJob).getImportType(); will(returnValue(AbstractImportTypeImpl.INSERT_UPDATE_TYPE));
				allowing(importJob).getCatalog(); will(returnValue(new CatalogImpl()));
				
				allowing(jobStatusHandler).getImportJobStatus(IMPORT_PROCESS_ID); 
				allowing(jobStatusHandler).reportCurrentRow(with(any(String.class)), with(any(int.class)));
				allowing(jobStatusHandler).reportImportJobState(with(any(String.class)), with(any(ImportJobState.class)));
				allowing(jobStatusHandler).isImportJobCancelled(IMPORT_PROCESS_ID); will(returnValue(true));
				
				allowing(cmUser).getGuid(); will(returnValue("GUID-1"));
				
				allowing(csvFileReader).close();
				allowing(changeSetService).isChangeSetEnabled(); will(returnValue(false));
			}
		});
		
		setupImportJobRunner();
	}

	private void setupImportJobRunner() {
		this.importJobRunnerImpl = new ImportJobRunnerProductAssociationImpl() {
			@Override
			protected boolean isEntityAlreadyImported(final String guid) {
				return true;
			}
			
			@Override
			protected CsvFileReader getCsvFileReader() {
				return csvFileReader;
			}
			
			@Override
			protected void updateContent(final String[] nextLine, final Persistable persistenceObject) {
				Product sourceProduct = new ProductImpl();
				sourceProduct.setCode(nextLine[0]);
				
				Product targetProduct = new ProductImpl();
				targetProduct.setCode(nextLine[1]);
				
				Catalog catalog = new CatalogImpl();
				catalog.setCode("TestStore");
				
				((ProductAssociation) persistenceObject).setSourceProduct(sourceProduct);
				((ProductAssociation) persistenceObject).setTargetProduct(targetProduct);
				((ProductAssociation) persistenceObject).setAssociationType(Integer.valueOf(nextLine[2]));
				((ProductAssociation) persistenceObject).setCatalog(catalog);
			}
			
		};
		
		importJobRunnerImpl.setElasticPath(elasticPath);
		importJobRunnerImpl.setUtility(this.utility);
		importJobRunnerImpl.setPersistenceEngine(persistenceEngine);
		importJobRunnerImpl.setImportService(importService);
		importJobRunnerImpl.setImportJobStatusHandler(jobStatusHandler);
		importJobRunnerImpl.setChangeSetService(changeSetService);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportJobRunnerProductAssociationImpl.run()' when the import CSV does not have duplicate data, 
	 * then the 'com.elasticpath.service.dataimport.impl.ImportJobStatusHandlerImpl.reportFailedRows()' is not invoked.
	 *
	 */
    @SuppressWarnings("unchecked")
    @Test
	public void testRunWithoutDuplicateData() {
		final List<String[]> csvLines = new ArrayList<String[]>();
		csvLines.add(new String[] { SOURCE_PRODUCT_CODE, "10000654", "4", "0", "1", "1" });
		csvLines.add(new String[] { SOURCE_PRODUCT_CODE, "10000888", "4", "0", "1", "2" });
		
		
		context.checking(new Expectations() {
			{
				int lineIndex = 0;
				
				allowing(csvFileReader).readNext(); will(returnValue(csvLines.get(lineIndex++)));
				oneOf(csvFileReader).getTopLines(importJobRunnerImpl.getCommitUnit()); will(returnValue(csvLines));
				
				never(jobStatusHandler).reportBadRows(with(equal(IMPORT_PROCESS_ID)), with(IsArray.<ImportBadRow>array(anything())));
				never(jobStatusHandler).reportFailedRows(with(equal(IMPORT_PROCESS_ID)), with(any(int.class)));
			}				
		});

		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, cmUser), IMPORT_PROCESS_ID);
		this.importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<String, Object>());
		this.importJobRunnerImpl.run();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportJobRunnerProductAssociationImpl.run()' when the import CSV has 2 rows of duplicate data, 
	 * then the 'com.elasticpath.service.dataimport.impl.ImportJobStatusHandlerImpl.reportFailedRows()' is invoked twice.
	 * 
	 */
    @SuppressWarnings("unchecked")
    @Test
	public void testRunWithDuplicateData() {
		
		final List<String[]> csvLines = new ArrayList<String[]>();
		csvLines.add(new String[] { SOURCE_PRODUCT_CODE, "10000654", "4", "0", "1", "1" });
		csvLines.add(new String[] { SOURCE_PRODUCT_CODE, "10000654", "4", "0", "1", "1" });   // duplicate row
		csvLines.add(new String[] { SOURCE_PRODUCT_CODE, "10000888", "4", "0", "1", "2" });
		csvLines.add(new String[] { SOURCE_PRODUCT_CODE, "10000888", "4", "0", "1", "2" });   // duplicate row
		
		context.checking(new Expectations() {
			{
				int lineIndex = 0;
				
				allowing(csvFileReader).readNext(); will(returnValue(csvLines.get(lineIndex++)));
				oneOf(csvFileReader).getTopLines(importJobRunnerImpl.getCommitUnit()); will(returnValue(csvLines));
				
				exactly(2).of(jobStatusHandler).reportBadRows(with(equal(IMPORT_PROCESS_ID)), with(IsArray.<ImportBadRow>array(anything())));
				exactly(2).of(jobStatusHandler).reportFailedRows(with(equal(IMPORT_PROCESS_ID)), with(any(int.class)));
			}
		});
		
		this.importJobRunnerImpl.init(getRequest(importJob, Locale.US, cmUser), IMPORT_PROCESS_ID);
		this.importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<String, Object>());
		this.importJobRunnerImpl.run();
	}
	
	private ImportJobRequest getRequest(final ImportJob importJob, final Locale locale, final CmUser cmUser) {
		ImportJobRequest request = new ImportJobRequestImpl(IMPORT_PROCESS_ID);
		request.setImportJob(importJob);
		request.setReportingLocale(locale);
		request.setInitiator(cmUser);
		request.setImportSource("file.csv");
		return request;
	}

}
