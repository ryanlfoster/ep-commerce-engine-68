package com.elasticpath.service.dataimport.impl;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Test;

import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCategoryImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerAddressImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeInventoryImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductCategoryAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductSkuImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.domain.pricing.csvimport.impl.ImportDataTypeBaseAmountImpl;
import com.elasticpath.domain.rules.csvimport.impl.ImportDataTypeCouponCodeEmailImpl;
import com.elasticpath.domain.rules.csvimport.impl.ImportDataTypeCouponCodeImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.impl.CsvFileReaderImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.dataimport.ImportJobExistException;
import com.elasticpath.service.dataimport.ImportJobRunner;
import com.elasticpath.test.factory.TestCustomerProfileFactory;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test <code>ImportServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods", "PMD.ExcessiveImports" })
public class ImportServiceImplTest extends AbstractEPServiceTestCase {

	private static final String PRODUCT_IMPORT_CSV = "productImport.csv";

	private static final String[] CSV_FILE_ROW1 = new String[] { "testIdRow1", "testNameRow1", "testValueRow1", "testParam1Row1", "testParam2Row1" };

	private static final String[] CSV_FILE_HEADER = new String[] { "id", "name", "value", "param1", "param2" };

	private ImportServiceImpl importService;

	private CatalogService catalogServiceMock;

	private ImportDataTypeProductImpl importDataTypeProduct;

	private ImportJob importJob;

	private ProductTypeImpl productType;

	private List<ProductType> productTypes;

	private ImportBadRow importBadRow;

	private ImportJobRunner mockImportJobRunner;

	private ImportJobRunner importJobRunner;

	private CategoryTypeImpl categoryType;

	private List<CategoryType> categoryTypes;

	private ImportDataTypeCategoryImpl importDataTypeCategory;

	private ImportDataTypeProductSkuImpl importDataTypeProductSku;

	private ImportDataTypeCustomerImpl importDataTypeCustomer;

	private ImportDataTypeCustomerAddressImpl importDataTypeCustomerAddress;

	private CsvFileReader mockCsvFileReader;

	@Mock
	private PriceListHelperService priceListHelperService;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		importService = new ImportServiceImpl() {
			@Override
			protected String getRemoteCsvFileName(final String csvFileName) {
				return csvFileName;
			}

			@Override
			public Currency getRequiredCurrency(final ImportJob importJob, final PriceListHelperService priceListHelperService) {
				return super.getRequiredCurrency(importJob, priceListHelperService);
			}
		};

		final AttributeService mockAttributeService = context.mock(AttributeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockAttributeService).getCustomerProfileAttributesMap();
				will(returnValue(new TestCustomerProfileFactory().getProfile()));
			}
		});
		stubGetBean(ContextIdNames.ATTRIBUTE_SERVICE, mockAttributeService);

		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_BASEAMOUNT, ImportDataTypeBaseAmountImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_CATEGORY, ImportDataTypeCategoryImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_COUPONCODE, ImportDataTypeCouponCodeImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_COUPONCODE_EMAIL, ImportDataTypeCouponCodeEmailImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_CUSTOMER, ImportDataTypeCustomerImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_CUSTOMER_ADDRESS, ImportDataTypeCustomerAddressImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_INVENTORY, ImportDataTypeInventoryImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT, ImportDataTypeProductImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_ASSOCIATION, ImportDataTypeProductAssociationImpl.class);
		stubGetBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_CATEGORY_ASSOCIATION, ImportDataTypeProductCategoryAssociationImpl.class);


		importService.setPersistenceEngine(getPersistenceEngine());

		catalogServiceMock = context.mock(CatalogService.class);
		importService.setCatalogService(catalogServiceMock);

		setupImportJob();

		setupProductTypes();

		setupCategoryTypes();

		// Must run after setupProductTypes();
		setupImportDataTypeProduct();

		setupImportDataTypeProductSku();

		setupImportDataTypeCategory();

		setupImportDataTypeCustomer();

		setupImportDataTypeCustomerAddress();

		setupImportBadRow();

		setupImportFault();

		setupImportJobRunner();

	}

	private void setupImportJob() {
		final String csvFileName = getClass().getClassLoader().getResource(PRODUCT_IMPORT_CSV).getFile();
		this.importJob = new ImportJobImpl();
		importJob.setCsvFileName(csvFileName);

		final Map<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put("product!name", Integer.valueOf(0));
		mappings.put("product!startDate", Integer.valueOf(1));
		mappings.put("product!defaultCategory", Integer.valueOf(2));
		final int indexNumberColumn4 = 3;
		mappings.put("product!guid", Integer.valueOf(indexNumberColumn4));
		importJob.setMappings(mappings);
	}

	private void setupCsvFileReader() {
		// Mock CsvFileReader
		mockCsvFileReader = context.mock(CsvFileReader.class);
		stubGetBean(ContextIdNames.CSV_FILE_READER, mockCsvFileReader);

		setupExpectationsForCsvFileReader();

	}

	private void setupImportDataTypeProduct() {
		// Mock an product import data type
		this.importDataTypeProduct = new ImportDataTypeProductImpl();
		importDataTypeProduct.init(this.productType);
	}

	private void setupImportDataTypeProductSku() {
		// Mock an product import data type
		this.importDataTypeProductSku = new ImportDataTypeProductSkuImpl();
		importDataTypeProductSku.init(this.productType);
	}

	private void setupImportDataTypeCategory() {
		// Mock an category import data type
		this.importDataTypeCategory = new ImportDataTypeCategoryImpl();
		importDataTypeCategory.init(this.categoryType);
	}

	private void setupImportDataTypeCustomer() {
		// Mock a Customer import data type
		this.importDataTypeCustomer = new ImportDataTypeCustomerImpl();
		importDataTypeCustomer.init(null);
	}

	private void setupImportDataTypeCustomerAddress() {
		// Mock a CustomerAddress import data type
		this.importDataTypeCustomerAddress = new ImportDataTypeCustomerAddressImpl();
		importDataTypeCustomerAddress.init(null);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.listImportJobs()'.
	 */
	@Test
	public void testListImportJobs() {
		final List<ImportJob> importJobs = new ArrayList<ImportJob>();

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("IMPORT_JOB_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(importJobs));
			}
		});

		assertSame(importJobs, importService.listImportJobs());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.count()'.
	 */
	@Test
	public void testCount() {
		final List<Long> returnValue = new ArrayList<Long>();
		returnValue.add(Long.valueOf(0));

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("JOB_COUNT"), with(any(Object[].class)));
				will(returnValue(returnValue));
			}
		});
		importService.count();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.remove()'.
	 */
	@Test
	public void testRemove() {
		final ImportJob importJob = new ImportJobImpl();
		importJob.setGuid("95696F2F-3AB2-E9B4-FBD9-91B7DCRWQWEQ");
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).executeNamedQuery("DELETE_IMPORT_JOB_STATUS_BY_JOB_GUID", importJob.getGuid());
				allowing(getMockPersistenceEngine()).executeNamedQuery("DELETE_IMPORT_NOTIFICATIONS_BY_JOB_GUID", importJob.getGuid());
				allowing(getMockPersistenceEngine()).delete(with(same(importJob)));
			}
		});
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.listImportDataTypes()'.
	 */
	@Test
	public void testListImportDataTypes() {
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_CATALOG_UIDS"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(1L)));
			}
		});
		final CatalogImpl catalogImpl = new CatalogImpl();
		catalogImpl.setMaster(true);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_BY_UID"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(catalogImpl)));
			}
		});

		final List<ImportDataType> importDataTypes = importService.listImportDataTypes();
		assertTrue(importDataTypes.size() > 1);
		assertEquals(this.importDataTypeProduct.getName(), importService.findImportDataType(this.importDataTypeProduct.getName()).getName());
	}

	/**
	 * Tests that if the catalog has PLAs, a currency is returned, or null - if it has not.
	 */
	@Test
	public void testGetRequiredCurrencyForProductImportDataType() {
		final Catalog catalogWithNoPrice = new CatalogImpl();
		catalogWithNoPrice.setCode("catalogWithNoPrice");
		final Catalog catalogWithPrice = new CatalogImpl();
		catalogWithPrice.setCode("catalogWithPrice");
		final Currency cad = Currency.getInstance(Locale.CANADA);
		final Set<Currency> currencySet = new HashSet<Currency>();
		currencySet.add(cad);
		context.checking(new Expectations() {
			{
				allowing(priceListHelperService).getAllCurrenciesFor(catalogWithNoPrice);
				will(returnValue(Collections.emptySet()));

				allowing(priceListHelperService).getAllCurrenciesFor(catalogWithPrice);
				will(returnValue(currencySet));
			}
		});
		importJob.setCatalog(catalogWithNoPrice);

		Currency result = importService.getRequiredCurrency(importJob, (priceListHelperService));
		assertNull(result);

		importJob.setCatalog(catalogWithPrice);
		result = importService.getRequiredCurrency(importJob, (priceListHelperService));
		assertNotNull(result);
		assertEquals(cad, result);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.getCatalogImportDataTypes()'.
	 */
	@Test
	public void testGetCatalogImportDataTypes() {
		// expectations
		final CatalogImpl catalogImpl = new CatalogImpl();
		catalogImpl.setMaster(true);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_BY_UID"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(catalogImpl)));
			}
		});

		final List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(1);
		assertTrue(importDataTypes.size() > 1);
		assertEquals(this.importDataTypeCategory.getName(), (importDataTypes.get(0)).getName());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.getCustomerImportDataTypes()'.
	 */
	@Test
	public void testGetCustomerImportDataTypes() {
		final List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		assertTrue(importDataTypes.size() > 1);
		assertEquals(this.importDataTypeCustomer.getName(), (importDataTypes.get(0)).getName());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.findImportJob(String)'.
	 */
	@Test
	public void testFindImportJob() {
		final List<ImportJob> importJobs = new ArrayList<ImportJob>();
		final ImportJob importJob = new ImportJobImpl();
		final String jobName = "ImportJob";
		importJob.setName(jobName);
		importJobs.add(importJob);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("IMPORT_JOB_FIND_BY_NAME", jobName);
				will(returnValue(importJobs));
			}
		});

		assertSame(importJob, importService.findImportJob(jobName));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.get(long)'.
	 */
	@Test
	public void testGet() {
		stubGetBean(ContextIdNames.IMPORT_JOB, ImportJob.class);

		final long uid = 1234L;
		final ImportJob importJob = new ImportJobImpl();

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(ImportJob.class, uid);
				will(returnValue(importJob));
			}
		});
		final ImportJob loadedImportJob = importService.getImportJob(uid);

		assertSame(importJob, loadedImportJob);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.getPreviewData(ImportJob importJob)'.
	 */
	@Test
	public void testGetPreviewData() {
		setupCsvFileReader();
		final List<String[]> topLines = new ArrayList<String[]>();
		topLines.add(new String[] { "id1", "id2", "id3", "id4" });
		context.checking(new Expectations() {
			{
				oneOf(mockCsvFileReader).getTopLines(with(any(Integer.class)));
				will(returnValue(topLines));
			}
		});

		assertEquals("The mappings of the import job should match the number of columns of the data",
				topLines.get(0).length, importJob.getMappings().size());

		assertEquals(1, importService.getPreviewData(importJob, 1).size());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.saveOrUpdate()'.
	 */
	@Test
	public void testSaveOrUpdateJobNotFound() {

		final List<ImportJob> importJobs = new ArrayList<ImportJob>();
		final ImportJob importJob = new ImportJobImpl();
		final ImportJob updatedImportJob = new ImportJobImpl();
		final String jobName = "ImportJob";
		importJob.setName(jobName);
		updatedImportJob.setName(jobName);
		importJobs.add(importJob);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("IMPORT_JOB_FIND_BY_NAME", jobName);
				will(returnValue(new ArrayList<ImportJob>()));
			}
		});

		// job name does not exist
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).merge(with(same(importJob)));
				will(returnValue(updatedImportJob));
			}
		});
		this.importService.saveOrUpdateImportJob(importJob);
	}

	@Test
	public void testSaveOrUpdate() {
		final List<ImportJob> importJobs = new ArrayList<ImportJob>();
		final ImportJob importJob = new ImportJobImpl();
		final ImportJob updatedImportJob = new ImportJobImpl();
		final String jobName = "ImportJob";
		importJob.setName(jobName);
		updatedImportJob.setName(jobName);
		importJobs.add(importJob);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("IMPORT_JOB_FIND_BY_NAME", jobName);
				will(returnValue(importJobs));
			}
		});

		// job name exists
		try {
			this.importService.saveOrUpdateImportJob(importJob);
		} catch (final ImportJobExistException e) {
			// Succeed
			assertNotNull(e);
		}

		importJobs.clear();
		importJob.setUidPk(1);
		importJobs.add(importJob);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).merge(with(same(importJob)));
				will(returnValue(updatedImportJob));
			}
		});

		// job name exists and is the same import job
		this.importService.saveOrUpdateImportJob(importJob);

		// job name exists and not the same import job
		final ImportJob importJob2 = new ImportJobImpl();
		importJob2.setName(jobName);
		importJob2.setUidPk(2);

		try {
			this.importService.saveOrUpdateImportJob(importJob2);
		} catch (final ImportJobExistException e) {
			// Succeed
			assertNotNull(e);
		}

	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.runImportJob()'.
	 */
	@Test
	public void testRunProductImport() {
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("FIND_ALL_CATALOG_UIDS");
				will(returnValue(Arrays.asList(1L)));
			}
		});
		final CatalogImpl catalogImpl = new CatalogImpl();
		catalogImpl.setMaster(true);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_BY_UID"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(catalogImpl)));
			}
		});

		importJob.setImportDataTypeName(importDataTypeProduct.getName());

		// Kick the import
//		this.importService.runImportJob(importJob);
	}

	private void setupProductTypes() {
		this.productType = new ProductTypeImpl();
		this.productType.setProductAttributeGroup(getAttributeGroup());
		this.productType.setSkuAttributeGroup(getAttributeGroup());
		this.productType.setSkuOptions(new HashSet<SkuOption>());

		productType.setName("AAA");

		this.productTypes = new ArrayList<ProductType>();
		productTypes.add(productType);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("PRODUCT_TYPE_BY_CATALOG_EAGER"), with(any(Object[].class)));
				will(returnValue(productTypes));
			}
		});
	}

	private AttributeGroup getAttributeGroup() {
		AttributeGroup attributeGroup = new AttributeGroupImpl();
		attributeGroup.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());

		return attributeGroup;
	}

	private void setupCategoryTypes() {
		this.categoryType = new CategoryTypeImpl();
		this.categoryType.setAttributeGroup(getAttributeGroup());

		categoryType.setName("AAA");
		categoryType.getAttributeGroup().setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		this.categoryTypes = new ArrayList<CategoryType>();
		categoryTypes.add(categoryType);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATEGORY_TYPE_BY_CATALOG_EAGER"), with(any(Object[].class)));
				will(returnValue(categoryTypes));
			}
		});
	}

	private void setupImportBadRow() {
		importBadRow = new ImportBadRowImpl();
		stubGetBean(ContextIdNames.IMPORT_BAD_ROW, importBadRow);
	}

	private void setupImportFault() {
		final ImportFault importFaultWarning = new ImportFaultImpl();
		importFaultWarning.setLevel(ImportFault.WARNING);
		stubGetBean(ContextIdNames.IMPORT_FAULT, importFaultWarning);
	}

	private void setupImportJobRunner() {
		this.mockImportJobRunner = context.mock(ImportJobRunner.class);
		this.importJobRunner = this.mockImportJobRunner;

		stubGetBean(ContextIdNames.IMPORT_JOB_RUNNER_PRODUCT, importJobRunner);
		context.checking(new Expectations() {
			{
				allowing(mockImportJobRunner).validate(with(any(Locale.class)));
				will(returnValue(new ArrayList<ImportFault>()));

				allowing(mockImportJobRunner).run();
			}
		});
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.getTitleLine(ImportJob importJob)'.
	 */
	@Test
	public void testGetTitleLine() {
		setupCsvFileReader();
		final ImportJobRequest importJobRequest = new ImportJobRequestImpl();
		importJobRequest.setImportJob(importJob);
		importJobRequest.setInitiator(new CmUserImpl());
		setupExpectationsForCsvFileReader();

		final int titlelineFields = 4;
		assertEquals(importService.getTitleLine(importJobRequest).size(), titlelineFields);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.listImportTypes()'.
	 */
	@Test
	public void testListImportTypes() {
		assertTrue(importService.listImportTypes().size() > 0);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.getImportType()'.
	 */
	@Test
	public void testgetImportType() {
		assertNotNull(importService.getImportType(1));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportServiceImpl.validateCsvFormat(ImportJob importJob)'.
	 */
	@Test
	public void testValidateCsvFormatProductImport() {
		final ImportJobRequest importJobRequest = new ImportJobRequestImpl();
		final ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileColDelimeter(',');
		importJob.setCsvFileTextQualifier('"');
		importJobRequest.setImportJob(importJob);
		importJobRequest.setInitiator(new CmUserImpl());

		stubGetBean(ContextIdNames.CSV_FILE_READER, new CsvFileReaderImpl() {
				@Override
				protected String getDatafileEncoding() {
					return "UTF-8";
				}
			}
		);

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String csvFileName = classLoader.getResource(PRODUCT_IMPORT_CSV).getFile();
		importJobRequest.setImportSource(csvFileName);
		assertEquals(0, importService.validateCsvFormat(importJobRequest).size());
	}

	@Test
	public void testValidateCsvFormatBadImport() {
		final ImportJobRequest importJobRequest = new ImportJobRequestImpl();
		final ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileColDelimeter(',');
		importJob.setCsvFileTextQualifier('"');
		importJobRequest.setImportJob(importJob);
		importJobRequest.setInitiator(new CmUserImpl());

		stubGetBean(ContextIdNames.CSV_FILE_READER, new CsvFileReaderImpl() {
				@Override
				protected String getDatafileEncoding() {
					return "UTF-8";
				}
			}
		);

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String csvFileName = classLoader.getResource("badImport.csv").getFile();
		importJobRequest.setImportSource(csvFileName);
		assertEquals(2, importService.validateCsvFormat(importJobRequest).size());
	}

	@Test
	public void testValidateCsvFormatWithWrappedTitle() {
		final ImportJobRequest importJobRequest = new ImportJobRequestImpl();
		final ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileColDelimeter(',');
		importJob.setCsvFileTextQualifier('"');
		importJobRequest.setImportJob(importJob);
		importJobRequest.setInitiator(new CmUserImpl());

		stubGetBean(ContextIdNames.CSV_FILE_READER, new CsvFileReaderImpl() {
				@Override
				protected String getDatafileEncoding() {
					return "UTF-8";
				}
			}
		);

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String csvFileName = classLoader.getResource("importFileWithWrappedTitleLine.csv").getFile();
		importJobRequest.setImportSource(csvFileName);
		assertEquals(1, importService.validateCsvFormat(importJobRequest).size());
	}

	@Test
	public void testValidateCsvFormatWithQuotesInTitle() {
		final ImportJobRequest importJobRequest = new ImportJobRequestImpl();
		final ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileColDelimeter(',');
		importJob.setCsvFileTextQualifier('"');
		importJobRequest.setImportJob(importJob);
		importJobRequest.setInitiator(new CmUserImpl());

		stubGetBean(ContextIdNames.CSV_FILE_READER, new CsvFileReaderImpl() {
					@Override
					protected String getDatafileEncoding() {
						return "UTF-8";
					}
			}
		);

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String csvFileName = classLoader.getResource("importFileWithQuotesInTitleLine.csv").getFile();
		importJob.setCsvFileTextQualifier('~');
		importJobRequest.setImportSource(csvFileName);
		assertEquals(1, importService.validateCsvFormat(importJobRequest).size());
	}

	/**
	 *
	 */
	private void setupExpectationsForCsvFileReader() {
		context.checking(new Expectations() {
			{
				allowing(mockCsvFileReader).open(with(anyOf(aNull(String.class), any(String.class))), with(any(char.class)), with(any(char.class)));
				allowing(mockCsvFileReader).close();

				allowing(mockCsvFileReader).readNext();
				will(onConsecutiveCalls(returnValue(CSV_FILE_HEADER), returnValue(CSV_FILE_ROW1), returnValue(null)));
			}
		});
	}


	/**
	 * Test that a paged list of catalog import jobs works as before and
	 * now when when restricting the catalogs to retrieve jobs for.
	 */
	@Test
	public void testListCatalogImportJobsPagedList() {

		final ImportJob job1 = createCatalogImportJob(1L);
		final ImportJob job2 = createCatalogImportJob(2L);
		final ImportJob job3 = createCatalogImportJob(3L);

		ImportServiceImpl importService = new ImportServiceImpl() {

			@Override
			List<ImportJob> listCatalogImportJobs() {
				List<ImportJob> importJobs = new ArrayList<ImportJob>();
				importJobs.add(job1);
				importJobs.add(job2);
				importJobs.add(job3);
				return importJobs;
			}
		};

		final int three = 3;
		final int biggerPage = 10;
		Long [] catalogUids = null;

		// Ensure previous behaviour unchanged, all jobs are still returned
		assertEquals(three, importService.countCatalogJobs(catalogUids));
		assertJobsSame(importService.listCatalogImportJobs(0, biggerPage, catalogUids), job1, job2, job3);

		// Ensure paged results works as before
		assertJobsSame(importService.listCatalogImportJobs(1, 1, catalogUids), job2);

		// Empty array should be equivalent to null - all jobs returned.
		catalogUids = new Long [] {};
		assertEquals(three, importService.countCatalogJobs(catalogUids));
		assertJobsSame(importService.listCatalogImportJobs(0, biggerPage, catalogUids), job1, job2, job3);


		catalogUids = new Long [] {1L};
		assertEquals(1, importService.countCatalogJobs(catalogUids));
		assertJobsSame(importService.listCatalogImportJobs(0, 0, catalogUids));
		assertJobsSame(importService.listCatalogImportJobs(0, 1, catalogUids), job1);
		assertJobsSame(importService.listCatalogImportJobs(0, biggerPage, catalogUids), job1);

		catalogUids = new Long [] {1L, 2L};
		assertEquals(2, importService.countCatalogJobs(catalogUids));
		assertJobsSame(importService.listCatalogImportJobs(0, biggerPage, catalogUids), job1, job2);

		catalogUids = new Long [] {1L, 2L, -1L};
		assertEquals(2, importService.countCatalogJobs(catalogUids));
		assertJobsSame(importService.listCatalogImportJobs(0, biggerPage, catalogUids), job1, job2);

	}

	private ImportJob createCatalogImportJob(final Long uid) {
		Catalog catalog1 = new CatalogImpl();
		catalog1.setUidPk(uid);
		final ImportJob job1 = new ImportJobImpl();
		job1.setCatalog(catalog1);
		return job1;
	}


	/**
	 * Test that a paged list of warehouse import jobs works as expected when restricting
	 * the warehouse to retrieve jobs for.
	 */
	@Test
	public void testListWarehouseImportJobsPagedList() {

		final ImportJob job1 = createWarehouseImportJob(1L);
		final ImportJob job2 = createWarehouseImportJob(2L);
		final ImportJob job3 = createWarehouseImportJob(3L);

		ImportServiceImpl importService = new ImportServiceImpl() {

			@Override
			List<ImportJob> listWarehouseImportJobs() {
				List<ImportJob> importJobs = new ArrayList<ImportJob>();
				importJobs.add(job1);
				importJobs.add(job2);
				importJobs.add(job3);
				return importJobs;
			}
		};

		final int three = 3;
		final int biggerPage = 10;
		Long [] warehouseUid = null;

		// Ensure previous behaviour unchanged, all jobs are still returned
		assertEquals(three, importService.countWarehouseJobs(warehouseUid));
		assertJobsSame(importService.listWarehouseImportJobs(0, biggerPage, warehouseUid), job1, job2, job3);

		// Ensure paged results works as before
		assertJobsSame(importService.listWarehouseImportJobs(1, 1, warehouseUid), job2);

		// Empty array should be equivalent to null - all jobs returned.
		warehouseUid = new Long [] {};
		assertEquals(three, importService.countWarehouseJobs(warehouseUid));
		assertJobsSame(importService.listWarehouseImportJobs(0, biggerPage, warehouseUid), job1, job2, job3);


		warehouseUid = new Long [] {1L};
		assertEquals(1, importService.countWarehouseJobs(warehouseUid));
		assertJobsSame(importService.listWarehouseImportJobs(0, 0, warehouseUid));
		assertJobsSame(importService.listWarehouseImportJobs(0, 1, warehouseUid), job1);
		assertJobsSame(importService.listWarehouseImportJobs(0, biggerPage, warehouseUid), job1);

		warehouseUid = new Long [] {1L, 2L};
		assertEquals(2, importService.countWarehouseJobs(warehouseUid));
		assertJobsSame(importService.listWarehouseImportJobs(0, biggerPage, warehouseUid), job1, job2);
	}

	private ImportJob createWarehouseImportJob(final Long warehouseUid) {
		Warehouse warehouse = new WarehouseImpl();
		warehouse.setUidPk(warehouseUid);
		final ImportJob job1 = new ImportJobImpl();
		job1.setWarehouse(warehouse);
		return job1;
	}


	/**
	 * Test that a paged list of customer import jobs works as expected
	 * when restricting the stores to retrieve jobs for.
	 */
	@Test
	public void testListCustomerImportJobsPagedListAndCount() {

		final ImportJob job1 = createCustomerImportJob(1L);
		final ImportJob job2 = createCustomerImportJob(2L);
		final ImportJob job3 = createCustomerImportJob(3L);

		ImportServiceImpl importService = new ImportServiceImpl() {

			@Override
			List<ImportJob> listCustomerImportJobs() {
				List<ImportJob> importJobs = new ArrayList<ImportJob>();
				importJobs.add(job1);
				importJobs.add(job2);
				importJobs.add(job3);
				return importJobs;
			}
		};

		final int three = 3;
		final int biggerPage = 10;
		Long [] storeUids = null;

		// Ensure previous behaviour unchanged, all jobs are still returned
		assertEquals(three, importService.countCustomerJobs(storeUids));
		assertJobsSame(importService.listCustomerImportJobs(0, biggerPage, storeUids), job1, job2, job3);

		// Ensure paged results works as before
		assertJobsSame(importService.listCustomerImportJobs(1, 1, storeUids), job2);

		// Empty array should be equivalent to null - all jobs returned.
		storeUids = new Long [] {};
		assertEquals(three, importService.countCustomerJobs(storeUids));
		assertJobsSame(importService.listCustomerImportJobs(0, biggerPage, storeUids), job1, job2, job3);


		storeUids = new Long [] {1L};
		assertEquals(1, importService.countCustomerJobs(storeUids));
		assertJobsSame(importService.listCustomerImportJobs(0, 0, storeUids));
		assertJobsSame(importService.listCustomerImportJobs(0, 1, storeUids), job1);
		assertJobsSame(importService.listCustomerImportJobs(0, biggerPage, storeUids), job1);

		storeUids = new Long [] {1L, 2L};
		assertEquals(2, importService.countCustomerJobs(storeUids));
		assertJobsSame(importService.listCustomerImportJobs(0, biggerPage, storeUids), job1, job2);

	}

	private ImportJob createCustomerImportJob(final Long storeUid) {
		Store store = new StoreImpl();
		store.setUidPk(storeUid);
		final ImportJob job1 = new ImportJobImpl();
		job1.setStore(store);
		return job1;
	}


	private void assertJobsSame(final List<ImportJob> returnedImportJobs, final ImportJob... jobs) {
		for (int x = 0; x < jobs.length; x++) {
			assertSame(jobs[x], returnedImportJobs.get(x));
		}
		assertEquals(jobs.length, returnedImportJobs.size());
	}

} // NOPMD
