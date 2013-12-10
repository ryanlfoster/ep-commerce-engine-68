package com.elasticpath.service.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Test;

import com.elasticpath.commons.beanframework.MessageSource;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.CatalogImportField;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.PrintWriter;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test <code>AbstractImportJobRunnerImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity", "PMD.CouplingBetweenObjects" })
public class AbstractImportJobRunnerImplTest extends AbstractEPServiceTestCase {

	private static final String[] CSV_FILE_ROW1 = new String[] { "testIdRow1", "testNameRow1", "testValueRow1", "testParam1Row1", "testParam2Row1" };

	private static final String[] CSV_FILE_HEADER = new String[] { "id", "name", "value", "param1", "param2" };

	private static final String DUMMY_EXCEPTION_MSG = "Dummy exception msg";

	private static final String IMPORT_FIELD3 = "importField3";

	private static final String IMPORT_FIELD2 = "importField2";

	private static final String IMPORT_FIELD1 = "importField1";

	private static final String IMPORT_DATA_TYPE_NAME = "Test Import Data Type";

	private static final String GUID_FIELD_NAME = IMPORT_FIELD1;

	private static final String DUMMY_STRING = "dummyString";

	private static final Object DEFAULT_GUID = "GUID-1";

	private AbstractImportJobRunnerImpl importJobRunnerImpl;

	private ImportJob importJob;

	private UtilityImpl utility;

	private EmailService mockEmailService;

	private ImportService mockImportService;

	private ImportService importService;

	private ImportDataType mockImportDataType;

	private ImportDataType importDataType;

	private ImportJob mockImportJob;

	private CatalogImportField mockImportField1;

	private ImportField importField1;

	private ImportField mockImportField2;

	private ImportField importField2;

	private ImportField mockImportField3;

	private ImportField importField3;

	private PersistenceSession mockPersistenceSession;

	private ImportGuidHelper mockImportGuidHelper;

	private Transaction mockTransaction;

	private PrintWriter mockPrintWriter;

	private CmUser mockCmUser;

	private Query mockQuery;

	private Catalog mockCatalog;

	private ImportJobStatusHandler mockJobStatusHandler;

	private CsvFileReader mockCsvFileReader;

	private ChangeSetService mockChangeSetService;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);

		this.mockPrintWriter = context.mock(PrintWriter.class);
		context.checking(new Expectations() {
			{
				allowing(mockPrintWriter).open(with(any(String.class)));
				allowing(mockPrintWriter).println(with(any(String.class)));
				allowing(mockPrintWriter).close();
			}
		});
		stubGetBean(ContextIdNames.PRINT_WRITER, mockPrintWriter);
		final MessageSource messageSource = context.mock(MessageSource.class);
		context.checking(new Expectations() {
			{
				allowing(messageSource).getMessage(
						with(any(String.class)), with(any(Object[].class)), with(any(String.class)), with(any(Locale.class)));
				will(returnValue(DUMMY_STRING));
			}
		});
		ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
		elasticPath.setMessageSource(messageSource);
		mockCsvFileReader = context.mock(CsvFileReader.class);
		mockJobStatusHandler = context.mock(ImportJobStatusHandler.class);
		mockChangeSetService = context.mock(ChangeSetService.class);
		mockImportGuidHelper = context.mock(ImportGuidHelper.class);

		setupUtility();

		setupImportJob();

		setupEmailService();

		setupPersistenceEngine();

		setupImportService();

		setupImportJobRunner(false);
	}

	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	@After
	public void tearDown() {
		ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
		elasticPath.setMessageSource(null);
	}

	@SuppressWarnings("PMD.CyclomaticComplexity")
	private void setupImportJobRunner(final boolean allowEntityFound) {
		this.importJobRunnerImpl = new AbstractImportJobRunnerImpl() {

			@Override
			protected Entity findEntityByGuid(final String guid) {
				if (allowEntityFound) {
					return new StubbedEntityImpl();
				}
				return null;
			}

			@Override
			protected Entity createNewEntity(final Object baseObject) {
				return new StubbedEntityImpl();
			}

			@Override
			protected int getCommitUnit() {
				return 1;
			}

			@Override
			protected void updateEntityBeforeSave(final Entity entity) {
				// do nothing
			}

			/**
			 *
			 * @return
			 */
			@Override
			protected CsvFileReader getCsvFileReader() {
				return mockCsvFileReader;
			}

		};

		context.checking(new Expectations() {
			{
				allowing(mockCsvFileReader).readNext();
				will(onConsecutiveCalls(
						returnValue(CSV_FILE_HEADER),
						returnValue(CSV_FILE_ROW1),
						returnValue(null))
				);
			}
		});

		this.importJobRunnerImpl.setUtility(this.utility);
		this.importJobRunnerImpl.setPersistenceEngine(getPersistenceEngine());
		this.importJobRunnerImpl.setImportService(importService);
		this.importJobRunnerImpl.setImportJobStatusHandler(mockJobStatusHandler);

		this.importJobRunnerImpl.setImportGuidHelper(this.mockImportGuidHelper);
		this.importJobRunnerImpl.setChangeSetService(mockChangeSetService);
	}

	private void setupImportDataType() {
		// Mock Import data type
		this.mockImportDataType = context.mock(ImportDataType.class);
		this.importDataType = mockImportDataType;
		context.checking(new Expectations() {
			{

				allowing(mockImportDataType).getGuidFieldName();
				will(returnValue(GUID_FIELD_NAME));

				allowing(mockImportDataType).getMetaObject();
				will(returnValue(new Object()));

				allowing(mockImportDataType).getImportField(IMPORT_FIELD1);
				will(returnValue(importField1));

				allowing(mockImportDataType).getImportField(IMPORT_FIELD2);
				will(returnValue(importField2));

				allowing(mockImportDataType).getImportField(IMPORT_FIELD3);
				will(returnValue(importField3));
			}
		});

		final Map<String, ImportField> importFields = new LinkedHashMap<String, ImportField>();
		importFields.put(IMPORT_FIELD1, importField1);
		importFields.put(IMPORT_FIELD2, importField2);
		importFields.put(IMPORT_FIELD3, importField3);
		context.checking(new Expectations() {
			{
				allowing(mockImportDataType).getImportFields();
				will(returnValue(importFields));
			}
		});
	}


	/**
	 * Setup the mock persistence session.
	 */
	private void setupPersistenceEngine() {

		this.mockPersistenceSession = context.mock(PersistenceSession.class);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).getSharedPersistenceSession();
				will(returnValue(mockPersistenceSession));
			}
		});

		this.mockTransaction = context.mock(Transaction.class);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceSession).beginTransaction();
				will(returnValue(mockTransaction));

				allowing(mockPersistenceSession).save(with(any(Persistable.class)));
				allowing(mockPersistenceSession).update(with(any(Persistable.class)));
				allowing(mockPersistenceSession).close();
			}
		});

		this.mockQuery = context.mock(Query.class);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceSession).createNamedQuery(with(any(String.class)));
				will(returnValue(mockQuery));
			}
		});
	}

	private void setupUtility() {
		this.utility = new UtilityImpl();
	}

	private void setupEmailService() {
		mockEmailService = context.mock(EmailService.class);
		context.checking(new Expectations() {
			{
				allowing(mockEmailService).sendMail(with(any(EmailProperties.class)));
			}
		});
	}

	private void setupImportService() {
		// Mock ImportService
		mockImportService = context.mock(ImportService.class);
		this.importService = mockImportService;

		// Mock methods called to ImportService
		context.checking(new Expectations() {
			{
				allowing(mockImportService).findImportDataType(with(any(String.class)));
				will(returnValue(importDataType));

				allowing(mockImportService).initImportDataTypeLocalesAndCurrencies(with(any(ImportDataType.class)), with(any(ImportJob.class)));
			}
		});
	}

	private void setupImportJob() {
		setupImportFields();

		setupImportDataType();

		// Mock Import job
		this.mockImportJob = context.mock(ImportJob.class);
		this.importJob = mockImportJob;
		context.checking(new Expectations() {
			{

				allowing(mockImportJob).getName();
				will(returnValue("test import job"));

				allowing(mockImportJob).getImportDataTypeName();
				will(returnValue(IMPORT_DATA_TYPE_NAME));
			}
		});
		final Map<String, Integer> importMappings = new HashMap<String, Integer>();
		importMappings.put(IMPORT_FIELD1, Integer.valueOf("1"));
		importMappings.put(IMPORT_FIELD2, Integer.valueOf("2"));
		importMappings.put(IMPORT_FIELD3, Integer.valueOf("3"));
		context.checking(new Expectations() {
			{
				allowing(mockImportJob).getMappings();
				will(returnValue(importMappings));

				allowing(mockImportJob).getCsvFileColDelimeter();
				will(returnValue(','));

				allowing(mockImportJob).getCsvFileTextQualifier();
				will(returnValue('"'));

				allowing(mockImportJob).getImportType();
				will(returnValue(AbstractImportTypeImpl.INSERT_UPDATE_TYPE));
			}
		});
		final Catalog catalog = new CatalogImpl();
		catalog.setMaster(true);
		catalog.setUidPk(1L);
		try {
			catalog.setSupportedLocales(createSupportedLocales());
		} catch (DefaultValueRemovalForbiddenException ex) {
			throw new IllegalStateException("Default locale should not be set", ex);
		}
		context.checking(new Expectations() {
			{
				allowing(mockImportJob).getCatalog();
				will(returnValue(catalog));
			}
		});

		final Store mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(mockStore).getSupportedLocales();
				will(returnValue(createSupportedLocales()));
			}
		});
		final String storeCode = "STORE CODE";
		context.checking(new Expectations() {
			{
				allowing(mockStore).getCode();
				will(returnValue(storeCode));

				allowing(mockImportJob).getStore();
				will(returnValue(mockStore));
			}
		});

		this.mockCmUser = context.mock(CmUser.class);
		context.checking(new Expectations() {
			{
				allowing(mockCmUser).getEmail();
				will(returnValue("admin@elasticpath.com"));
			}
		});

		this.mockCatalog = context.mock(Catalog.class);
	}

	private Collection<Locale> createSupportedLocales() {
		Collection<Locale> locales = new ArrayList<Locale>();
		locales.add(Locale.US);
		locales.add(Locale.CANADA);
		locales.add(Locale.CANADA_FRENCH);
		return locales;
	}

	private void setupImportFields() {
		// Mock Import Fields
		this.mockImportField1 = context.mock(CatalogImportField.class);
		this.importField1 = mockImportField1;
		context.checking(new Expectations() {
			{
				allowing(mockImportField1).getName();
				will(returnValue(IMPORT_FIELD1));

				allowing(mockImportField1).getType();
				will(returnValue("string"));

				allowing(mockImportField1).setCatalog(with(any(Catalog.class)));
			}
		});

		this.mockImportField2 = context.mock(ImportField.class);
		this.importField2 = mockImportField2;
		context.checking(new Expectations() {
			{
				allowing(mockImportField2).getName();
				will(returnValue(IMPORT_FIELD2));

				allowing(mockImportField2).getType();
				will(returnValue("string"));
			}
		});

		this.mockImportField3 = context.mock(ImportField.class, "import field 3");
		this.importField3 = mockImportField3;
		context.checking(new Expectations() {
			{
				allowing(mockImportField3).getName();
				will(returnValue(IMPORT_FIELD3));

				allowing(mockImportField3).getType();
				will(returnValue("string"));
			}
		});
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.validate()'.
	 */
	@Test
	public void testValidateWithoutError() {
		context.checking(new Expectations() {
			{
				allowing(mockImportDataType).isEntityImport();
				will(returnValue(true));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(false));

				allowing(mockImportField1).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField2).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField3).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
			}
		});

		context.checking(new Expectations() {
			{

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));

				oneOf(mockCsvFileReader).close();
			}
		});

		String importJobProcessId = "id3";
		context.checking(new Expectations() {
			{

				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));
			}
		});
		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockCmUser), importJobProcessId);
		final List<ImportBadRow> result = this.importJobRunnerImpl.validate(Locale.CANADA);
		assertEquals(0, result.size());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.validate()'.
	 */
	@Test
	public void testValidateWithError() {
		stubGetBean(ContextIdNames.IMPORT_BAD_ROW, ImportBadRowImpl.class);
		stubGetBean(ContextIdNames.IMPORT_FAULT, ImportFaultImpl.class);
		context.checking(new Expectations() {
			{


				allowing(mockImportDataType).isEntityImport();
				will(returnValue(true));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(false));


				allowing(mockImportField1).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				will(throwException(new EpBindException(DUMMY_EXCEPTION_MSG)));

				allowing(mockImportField2).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				will(throwException(new EpBindException(DUMMY_EXCEPTION_MSG)));

				allowing(mockImportField3).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));

				oneOf(mockCsvFileReader).close();
			}
		});

		context.checking(new Expectations() {
			{

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));
			}
		});

		String importJobProcessId = "id5";

		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockCmUser), importJobProcessId);
		final List<ImportBadRow> result = this.importJobRunnerImpl.validate(Locale.CANADA);
		assertEquals(1, result.size());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.validate()'.
	 */
	@Test
	public void testValidateForImportDataTypeNotAllowNewEntityCreationWithoutError() {
		this.setupImportJobRunner(true);
		context.checking(new Expectations() {
			{
				allowing(mockImportDataType).isEntityImport();
				will(returnValue(false));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(true));

				allowing(mockImportDataType).createValueObject();
				will(returnValue(new AbstractPersistableImpl() {
					private static final long serialVersionUID = -1870900109084971948L;
					private long uidPk;
					public long getUidPk() {
						return this.uidPk;
					}
					public void setUidPk(final long newUidPk) {
						this.uidPk = newUidPk;
					}
				}));

				allowing(mockImportDataType).saveOrUpdate(with(any(Entity.class)), with(any(Persistable.class)));

				allowing(mockImportField1).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField2).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField3).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));
			}
		});

		String importJobProcessId = "id4";

		context.checking(new Expectations() {
			{
				oneOf(mockCsvFileReader).close();

				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));
			}
		});

		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockCmUser), importJobProcessId);
		final List<ImportBadRow> result = this.importJobRunnerImpl.validate(Locale.CANADA);
		assertEquals(0, result.size());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.validate()'.
	 */
	@Test
	public void testValidateForImportDataTypeNotAllowNewEntityCreationWithError() {
		this.setupImportJobRunner(true);
		context.checking(new Expectations() {
			{
				allowing(mockImportDataType).isEntityImport();
				will(returnValue(true));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(false));

				allowing(mockImportField1).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField2).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField3).checkStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));

				oneOf(mockCsvFileReader).close();

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));
			}
		});

		String importJobProcessId = "id9";
		context.checking(new Expectations() {
			{
				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));
			}
		});

		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockCmUser), importJobProcessId);
		final List<ImportBadRow> result = this.importJobRunnerImpl.validate(Locale.CANADA);
		assertEquals(0, result.size());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.getUtility()'.
	 */
	@Test
	public void testGetUtility() {
		assertSame(utility, importJobRunnerImpl.getUtility());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.run()'.
	 */
	@Test
	public void testRunWithoutError() {
		context.checking(new Expectations() {
			{
				allowing(mockImportDataType).isEntityImport();
				will(returnValue(true));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(false));

				allowing(mockImportField1).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField2).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField3).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(true));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));

				allowing(mockTransaction).commit();
				allowing(mockQuery).setParameter(with(any(int.class)), with(any(Object.class)));
			}
		});

		final List<Catalog> catalogList = new ArrayList<Catalog>();
		catalogList.add(mockCatalog);
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(catalogList));
			}
		});

		final String importJobProcessId = "id1";
		final CmUser mockUser = mockCmUserWithDefaultGuid();

		context.checking(new Expectations() {
			{
				oneOf(mockJobStatusHandler).reportImportJobState(importJobProcessId, ImportJobState.FINISHED);
			}
		});

		final ImportJobStatus importJobStatusMock = context.mock(ImportJobStatus.class);
		context.checking(new Expectations() {
			{
				allowing(importJobStatusMock).getTotalRows();
				will(returnValue(2));

				allowing(importJobStatusMock).getCurrentRow();
				will(returnValue(2));

				allowing(importJobStatusMock).getFailedRows();
				will(returnValue(0));

				allowing(importJobStatusMock).getLeftRows();
				will(returnValue(0));

				allowing(importJobStatusMock).getImportJob();
				will(returnValue(importJob));

				oneOf(mockJobStatusHandler).getImportJobStatus(with(any(String.class)));
				will(returnValue(importJobStatusMock));

				oneOf(mockCsvFileReader).close();

				oneOf(mockCsvFileReader).getTopLines(with(any(int.class)));
				will(returnValue(Arrays.<String[]>asList()));

				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));
			}
		});

		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockUser), importJobProcessId);
		this.importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<String, Object>());
		this.importJobRunnerImpl.run();

	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.run()'.
	 */
	@Test
	public void testRunWithError() {
		context.checking(new Expectations() {
			{
				allowing(mockImportDataType).isEntityImport();
				will(returnValue(true));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(false));


				allowing(mockImportField1).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				will(throwException(new EpBindException(DUMMY_EXCEPTION_MSG)));

				allowing(mockImportField2).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField3).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(true));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));
			}
		});

		final CmUser mockUser = mockCmUserWithDefaultGuid();

		final ImportJobStatus importJobStatusMock = context.mock(ImportJobStatus.class);
		context.checking(new Expectations() {
			{
				allowing(importJobStatusMock).getTotalRows();
				will(returnValue(2));

				allowing(importJobStatusMock).getCurrentRow();
				will(returnValue(2));

				allowing(importJobStatusMock).getFailedRows();
				will(returnValue(0));

				allowing(importJobStatusMock).getLeftRows();
				will(returnValue(0));

				allowing(importJobStatusMock).getImportJob();
				will(returnValue(importJob));

				oneOf(mockJobStatusHandler).getImportJobStatus(with(any(String.class)));
				will(returnValue(importJobStatusMock));


				allowing(mockImportJob).getMaxAllowErrors();
				will(returnValue(0));

				allowing(mockQuery).setParameter(with(any(int.class)), with(any(Object.class)));
			}
		});

		final List<Catalog> catalogList = new ArrayList<Catalog>();
		catalogList.add(mockCatalog);
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(catalogList));

				allowing(mockJobStatusHandler).reportBadRows(with(any(String.class)), with(any(ImportBadRow.class)));
				allowing(mockJobStatusHandler).reportCurrentRow(with(any(String.class)), with(any(int.class)));
				allowing(mockJobStatusHandler).reportFailedRows(with(any(String.class)), with(any(int.class)));
				allowing(mockJobStatusHandler).reportImportJobState(with(any(String.class)), with(any(ImportJobState.class)));

				allowing(mockJobStatusHandler).verifyImportJobFailedRows(with(any(String.class)), with(any(int.class)));
				will(returnValue(true));

				oneOf(mockCsvFileReader).close();

				oneOf(mockCsvFileReader).getTopLines(with(any(int.class)));
				will(returnValue(Arrays.<String[]>asList()));

				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));

				allowing(mockTransaction).rollback();
			}
		});

		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockUser), "id2");
		this.importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<String, Object>());
		this.importJobRunnerImpl.run();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.getImportService()'.
	 */
	@Test
	public void testGetImportService() {
		assertSame(importService, this.importJobRunnerImpl.getImportService());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.postHandlings()'.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@Test
	public void testPostHandlings() {
		stubGetBean(ContextIdNames.IMPORT_BAD_ROW, ImportBadRowImpl.class);
		stubGetBean(ContextIdNames.IMPORT_FAULT, ImportFaultImpl.class);
		context.checking(new Expectations() {
			{

				allowing(mockImportDataType).isEntityImport();
				will(returnValue(true));

				allowing(mockImportDataType).isValueObjectImport();
				will(returnValue(false));


				allowing(mockImportField1).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				will(throwException(new EpBindException(DUMMY_EXCEPTION_MSG)));

				allowing(mockImportField2).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));
				allowing(mockImportField3).setStringValue(with(any(Object.class)), with(any(String.class)), with(any(ImportGuidHelper.class)));

				allowing(mockImportField1).isCatalogObject();
				will(returnValue(true));

				allowing(mockImportField2).isCatalogObject();
				will(returnValue(false));

				allowing(mockImportField3).isCatalogObject();
				will(returnValue(false));
			}
		});

		final CmUser mockUser = mockCmUserWithDefaultGuid();

		final String importJobProcessId = "id1";
		this.importJobRunnerImpl.init(getRequest(this.importJob, Locale.US, mockUser), importJobProcessId);

		context.checking(new Expectations() {
			{
				exactly(2).of(mockJobStatusHandler).reportCurrentRow(importJobProcessId, 1);
				oneOf(mockJobStatusHandler).reportImportJobState(importJobProcessId, ImportJobState.FAILED);

				oneOf(mockJobStatusHandler).reportBadRows(with(any(String.class)), with(any(ImportBadRow[].class)));
				oneOf(mockJobStatusHandler).reportFailedRows(with(any(String.class)), with(any(int.class)));

				oneOf(mockJobStatusHandler).verifyImportJobFailedRows(with(any(String.class)), with(any(int.class)));
				will(returnValue(false));
			}
		});

		final ImportJobStatus importJobStatusMock = context.mock(ImportJobStatus.class);
		context.checking(new Expectations() {
			{
				allowing(importJobStatusMock).getTotalRows();
				will(returnValue(2));

				allowing(importJobStatusMock).getCurrentRow();
				will(returnValue(2));

				allowing(importJobStatusMock).getFailedRows();
				will(returnValue(0));

				allowing(importJobStatusMock).getLeftRows();
				will(returnValue(0));

				allowing(importJobStatusMock).getImportJob();
				will(returnValue(importJob));

				oneOf(mockJobStatusHandler).getImportJobStatus(with(any(String.class)));
				will(returnValue(importJobStatusMock));

				oneOf(mockCsvFileReader).close();
			}
		});

		final List<String[]> rows = new ArrayList<String[]>();
		rows.add(CSV_FILE_ROW1);
		context.checking(new Expectations() {
			{
				oneOf(mockCsvFileReader).getTopLines(1);
				will(onConsecutiveCalls(
						returnValue(rows),
						returnValue(Collections.emptyList())
				));

				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));

				allowing(getMockPersistenceEngine()).isCacheEnabled();
				will(returnValue(false));

				allowing(mockImportJob).getMaxAllowErrors();
				will(returnValue(0));

				allowing(mockQuery).setParameter(with(any(int.class)), with(any(Object.class)));
			}
		});

		final List<Catalog> catalogList = new ArrayList<Catalog>();
		catalogList.add(mockCatalog);
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(catalogList));

				allowing(mockTransaction).rollback();
			}
		});

		this.importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<String, Object>());

		this.importJobRunnerImpl.run();
	}

	private CmUser mockCmUserWithDefaultGuid() {
		final CmUser mockUser = context.mock(CmUser.class, "other cmuser");
		context.checking(new Expectations() {
			{
				allowing(mockUser).getEmail();

				allowing(mockUser).getGuid();
				will(returnValue(DEFAULT_GUID));
			}
		});
		return mockUser;
	}

	private ImportJobRequest getRequest(final ImportJob importJob, final Locale locale, final CmUser cmUser) {
		ImportJobRequest request = new ImportJobRequestImpl();
		request.setImportJob(importJob);
		request.setReportingLocale(locale);
		request.setInitiator(cmUser);
		request.setImportSource("file.csv");
		return request;
	}


	/**
	 * Stubbed entity!
	 */
	private class StubbedEntityImpl extends AbstractEntityImpl {
		private static final long serialVersionUID = 1L;
		private long uidPk;
		private String guid;

		public String getGuid() {
			return guid;
		}

		public void setGuid(final String guid) {
			this.guid = guid;
		}

		public long getUidPk() {
			return this.uidPk;
		}

		public void setUidPk(final long newUidPk) {
			this.uidPk = newUidPk;
		}
	}
}
