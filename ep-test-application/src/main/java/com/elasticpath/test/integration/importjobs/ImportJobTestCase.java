package com.elasticpath.test.integration.importjobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCategoryImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerAddressImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeInventoryImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductCategoryAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.ImportJobScenario;
import com.elasticpath.test.util.Utils;

/**
 * Provides additional setup and services for import testing. Note that the Db is reset for each test so that the data imported after each test is
 * guaranteed not to exist in the database.<br>
 * NOTE: CSV files to be imported can be found in ${EP_DIR}/integrationtests/assets/import directory.
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class ImportJobTestCase extends BasicSpringContextTest {

	private static final String PRODUCT_SKU_PREFIX = "ProductSku - ";

	private static final String PRODUCT_PREFIX = "Product - ";

	/** CSV file text strings qualifier. */
	public static final char CSV_FILE_TEXT_QUALIFIER = '"';

	/** CSV file column delimiter. */
	public static final char CSV_FILE_COL_DELIMETER = '|';

	/** Import Data type name. */
	public static final String DEFAULT_IMPORT_DATA_TYPE = "Sample Data Type";

	@Autowired protected ImportService importService;

	protected ImportJobScenario scenario;

	protected TestDataPersisterFactory persisterFactory;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception if an error occurs 
	 */
	@Before
	public void setUp() throws Exception {
		createTestData();
		persisterFactory = getTac().getPersistersFactory();
	}

	protected void createTestData() {
		scenario = getTac().useScenario(ImportJobScenario.class);
	}

	/**
	 * Helper to create an import jobs for testing purposes.
	 * 
	 * @param associatedDomainObject the associate domain object (store, catalog or warehouse)
	 * @param name the name of the import job
	 * @param csvFileName the name of the csv file
	 * @param importType the import type
	 * @param importDataTypeName the import data type name. Should be only the name of the import file without any parent folders specified.
	 * @param mappings the map of import mappings
	 * @return the persisted importJob
	 */
	protected ImportJob createSimpleImportJob(final Persistable associatedDomainObject, final String name, final String csvFileName,
			final ImportType importType, final String importDataTypeName, final Map<String, Integer> mappings) {
		ImportJob importJob = getBeanFactory().getBean(ContextIdNames.IMPORT_JOB);
		if (associatedDomainObject instanceof Store) {
			importJob.setStore((Store) associatedDomainObject);
		} else if (associatedDomainObject instanceof Catalog) {
			importJob.setCatalog((Catalog) associatedDomainObject);
		} else if (associatedDomainObject instanceof Warehouse) {
			importJob.setWarehouse((Warehouse) associatedDomainObject);
		} else {
			throw new IllegalArgumentException("Store, Catalog or Warehouse object expected for associatedDomainObject.");
		}

		importJob.setImportDataTypeName(importDataTypeName);
		importJob.setImportType(importType);
		importJob.setName(name);
		importJob.setCsvFileName(csvFileName);
		importJob.setCsvFileColDelimeter(CSV_FILE_COL_DELIMETER);
		importJob.setCsvFileTextQualifier(CSV_FILE_TEXT_QUALIFIER);
		importJob.setMappings(mappings);

		return importService.saveOrUpdateImportJob(importJob);
	}

	/**
	 * Helper to create fake import job, which means that most of parameters are predefined.<br>
	 * Such a job is useful to test basic import service operations like import job insertion or removing.
	 * 
	 * @param importType the job's import type.
	 * @return transient initialized import job.
	 */
	protected ImportJob createDefaultImportJob(final ImportType importType) {
		final ImportJob importJob = new ImportJobImpl();
		importJob.setName(Utils.uniqueCode("ImportJob"));
		importJob.setCsvFileName(Utils.uniqueCode("CsvName"));
		importJob.setCatalog(scenario.getCatalog());
		importJob.setStore(scenario.getStore());
		importJob.setWarehouse(scenario.getWarehouse());
		importJob.initialize();
		importJob.setCsvFileColDelimeter(CSV_FILE_COL_DELIMETER);
		importJob.setCsvFileTextQualifier(CSV_FILE_TEXT_QUALIFIER);

		importJob.setImportType(importType);
		importJob.setImportDataTypeName(DEFAULT_IMPORT_DATA_TYPE);
		importJob.setMappings(new HashMap<String, Integer>());
		return importJob;
	}

	public List<ImportBadRow> executeImportJob(final ImportJob importJob) throws InterruptedException {
		CmUser initiator = scenario.getCmUser();

		ImportJobRequest importJobProcessRequest = new ImportJobRequestImpl();
		importJobProcessRequest.setImportJob(importJob);
		importJobProcessRequest.setImportSource(importJob.getCsvFileName());
		importJobProcessRequest.setInitiator(initiator);
		importJobProcessRequest.setReportingLocale(Locale.getDefault());

		ImportJobStatus status = importService.scheduleImport(importJobProcessRequest);
		
		status = new ImportJobProcessorLauncher(getBeanFactory()).launchAndWaitToFinish(status);
		
		return status.getBadRows();
	}

	protected ImportDataType findByType(final List<ImportDataType> importDataTypes, final Class< ? extends ImportDataType> type) {

		for (ImportDataType importDataType : importDataTypes) {
			if (type.isInstance(importDataType)) {
				return importDataType;
			}
		}
		return null;
	}

	protected ImportJob createInsertCustomerImportJob() {
		return createInsertCustomerImportJob("customer_insert.csv");
	}
	
	protected ImportJob createInsertCustomerImportInvalidJob() {
		return createInsertCustomerImportJob("customer_insert_invalid.csv");
	}
	
	protected ImportJob createInsertCustomerImportJob(final String csvFileName) {
		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("guid", 1);
		mappings.put("userId", 2);
		mappings.put("CP_FIRST_NAME", 3);
		mappings.put("CP_LAST_NAME", 4);
		mappings.put("CP_EMAIL", 5);
		mappings.put("CP_ANONYMOUS_CUST", 6);
		mappings.put("CP_HTML_EMAIL", 7);
		mappings.put("status", 8);
		mappings.put("creationDate", 9);
		mappings.put("CP_PHONE", 10);

		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Insert Customers"),
				csvFileName, AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateCustomerImportJob() {
		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("guid", 1);
		mappings.put("userId", 2);
		mappings.put("CP_FIRST_NAME", 3);
		mappings.put("CP_LAST_NAME", 4);
		mappings.put("CP_EMAIL", 5);
		mappings.put("CP_ANONYMOUS_CUST", 6);
		mappings.put("CP_HTML_EMAIL", 7);
		mappings.put("status", 8);
		mappings.put("creationDate", 9);
		mappings.put("CP_PHONE", 10);

		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Insert and Update Customers"),
				"customer_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createUpdateCustomerImportJob() {
		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("guid", 1);
		mappings.put("CP_EMAIL", 2);
		mappings.put("CP_PHONE", 3);

		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Update Customers"),
				"customer_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createDeleteCustomerImportJob() {
		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("guid", 1);

		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Delete Customers"),
				"customer_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertCategoriesImportJob() {

		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCategoryImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put("categoryCode", 1);
		mappings.put("parentCategoryCode", 2);
		mappings.put("displayName(en)", 3);
		mappings.put("enableDate", 4);
		mappings.put("disableDate", 5);
		mappings.put("storeVisible", 6);
		mappings.put("ordering", 7);
		mappings.put("seoUrl(en)", 8);
		mappings.put("seoTitle(en)", 9);
		mappings.put("seoKeyWords(en)", 10);
		mappings.put("seoDescription(en)", 11);
		mappings.put("catImage", 12);
		mappings.put("catDescription(en)", 13);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert Categories"),
				"categories_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateCategoriesImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = importDataTypes.get(0).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put("categoryCode", 1);
		mappings.put("parentCategoryCode", 2);
		mappings.put("displayName(en)", 3);
		mappings.put("enableDate", 4);
		mappings.put("disableDate", 5);
		mappings.put("storeVisible", 6);
		mappings.put("ordering", 7);
		mappings.put("seoUrl(en)", 8);
		mappings.put("seoTitle(en)", 9);
		mappings.put("seoKeyWords(en)", 10);
		mappings.put("seoDescription(en)", 11);
		mappings.put("catImage", 12);
		mappings.put("catDescription(en)", 13);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert and Update Categories"),
				"categories_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createUpdateCategoriesImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = importDataTypes.get(0).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put("categoryCode", 1);
		mappings.put("displayName(en)", 2);
		mappings.put("catDescription(en)", 3);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Update Categories"),
				"categories_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createDeleteCategoriesImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = importDataTypes.get(0).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put("categoryCode", 1);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Delete Categories"),
				"categories_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	/**
	 * The job should contain relatively many row in order to keep import pending before second import. <br>
	 * Required for concurrent import and similar tests.
	 * 
	 * @return the import job.
	 */
	protected ImportJob createInsertCategoriesImportJobManyRows() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = importDataTypes.get(0).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put("categoryCode", 1);
		mappings.put("displayName(en)", 2);
		mappings.put("parentCategoryCode", 3);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Concurrent Import Categories"),
				"categories_insert_concurrent.csv", AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertCategoryAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductCategoryAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);
		mappings.put("categoryCode", 2);
		mappings.put("featuredProductOrder", 3);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert Categories"),
				"categoryassociation_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateCategoryAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductCategoryAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);
		mappings.put("categoryCode", 2);
		mappings.put("featuredProductOrder", 3);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert and Update Categories"),
				"categoryassociation_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, importDataTypeName,
				mappings);
		return importJob;
	}

	protected ImportJob createUpdateCategoryAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductCategoryAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);
		mappings.put("featuredProductOrder", 2);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Update Categories"),
				"categoryassociation_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createDeleteCategoryAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductCategoryAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Delete Categories"),
				"categoryassociation_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createUpdateCustomerAddressImportJob() {
		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerAddressImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("guid", 1);
		mappings.put("customerGuid", 2);
		mappings.put("firstName", 3);
		mappings.put("lastName", 4);
		mappings.put("phoneNumber", 5);
		mappings.put("street1", 6);
		mappings.put("street2", 7);
		mappings.put("city", 8);
		mappings.put("subCountry", 9);
		mappings.put("country", 10);
		mappings.put("zipOrPostalCode", 11);

		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Update Customer Addresses"),
				"customeraddress_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertInventoryImportJob() {
		List<ImportDataType> importDataTypes = importService.getWarehouseImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeInventoryImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productSku", 1);
		mappings.put("quantityOnHand", 2);
		mappings.put("reservedQuantity", 3);
		mappings.put("reorderMinimum", 4);
		mappings.put("reorderQuantity", 5);
		mappings.put("restockDate", 6);

		ImportJob importJob = createSimpleImportJob(scenario.getWarehouse(), Utils.uniqueCode("Insert Inventories"),
				"inventory_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateInventoryImportJob() {
		List<ImportDataType> importDataTypes = importService.getWarehouseImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeInventoryImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productSku", 1);
		mappings.put("quantityOnHand", 2);
		mappings.put("reservedQuantity", 3);
		mappings.put("reorderMinimum", 4);
		mappings.put("reorderQuantity", 5);
		mappings.put("restockDate", 6);

		ImportJob importJob = createSimpleImportJob(scenario.getWarehouse(), Utils.uniqueCode("Insert and Update Categories"),
				"inventory_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createUpdateInventoryImportJob() {
		List<ImportDataType> importDataTypes = importService.getWarehouseImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeInventoryImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productSku", 1);
		mappings.put("quantityOnHand", 2);
		mappings.put("reservedQuantity", 3);
		mappings.put("reorderMinimum", 4);
		mappings.put("reorderQuantity", 5);
		mappings.put("restockDate", 6);

		ImportJob importJob = createSimpleImportJob(scenario.getWarehouse(), Utils.uniqueCode("Update Categories"),
				"inventory_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createDeleteInventoryImportJob() {
		List<ImportDataType> importDataTypes = importService.getWarehouseImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeInventoryImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productSku", 1);

		ImportJob importJob = createSimpleImportJob(scenario.getWarehouse(), Utils.uniqueCode("Delete Categories"),
				"inventory_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertProductAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("sourceProductCode", 1);
		mappings.put("targetProductCode", 2);
		mappings.put("associationType", 3);
		mappings.put("sourceProductDependant", 4);
		mappings.put("defaultQuantity", 5);
		mappings.put("ordering", 6);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert Categories"),
				"productassociation_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateProductAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("sourceProductCode", 1);
		mappings.put("targetProductCode", 2);
		mappings.put("associationType", 3);
		mappings.put("sourceProductDependant", 4);
		mappings.put("defaultQuantity", 5);
		mappings.put("ordering", 6);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert and Update Categories"),
				"productassociation_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, importDataTypeName,
				mappings);
		return importJob;
	}

	protected ImportJob createUpdateProductAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("sourceProductCode", 1);
		mappings.put("defaultQuantity", 2);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Update Categories"),
				"productassociation_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createDeleteProductAssociationImportJob() {
		List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductAssociationImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("sourceProductCode", 1);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Delete Categories"),
				"productassociation_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	protected ImportJob createInsertProductSkuImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Digital cameras SKUs
		mappings.put("skuCode", 1);
		mappings.put("image", 3);
		mappings.put("productCode", 4);
		mappings.put("Color", 5);
		mappings.put("listPrice(USD)", 6);
		mappings.put("salePrice(USD)", 7);
		mappings.put("width", 8);
		mappings.put("length", 9);
		mappings.put("height", 10);
		mappings.put("weight", 11);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert ProductSku"),
				"productsku_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, PRODUCT_SKU_PREFIX
						+ ImportJobScenario.MULTI_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateProductSkuImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("skuCode", 1);
		mappings.put("productCode", 2);
		mappings.put("image", 3);
		mappings.put("productGuid", 4);
		mappings.put("color", 5);
		mappings.put("listPrice_USD", 6);
		mappings.put("salePrice_USD", 7);
		mappings.put("width", 8);
		mappings.put("length", 9);
		mappings.put("height", 10);
		mappings.put("weight", 11);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert and Update ProductSku"),
				"productsku_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, PRODUCT_SKU_PREFIX
						+ ImportJobScenario.MULTI_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createUpdateProductSkuImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("skuCode", 1);
		mappings.put("productCode", 2);
		mappings.put("image", 3);
		mappings.put("productGuid", 4);
		mappings.put("color", 5);
		mappings.put("listPrice_USD", 6);
		mappings.put("salePrice_USD", 7);
		mappings.put("width", 8);
		mappings.put("length", 9);
		mappings.put("height", 10);
		mappings.put("weight", 11);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Update Categories"),
				"productsku_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, PRODUCT_SKU_PREFIX
						+ ImportJobScenario.MULTI_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Telescopes
		mappings.put("skuCode", 1);
		mappings.put("productCode", 2);
		mappings.put("description(en)", 3);
		mappings.put("description(fr)", 4);
		mappings.put("displayName(en)", 5);
		mappings.put("displayName(fr)", 6);
		mappings.put("image", 7);
		mappings.put("defaultCategoryCode", 8);
		mappings.put("brandCode", 9);
		mappings.put("listPrice(USD)", 10);
		mappings.put("salePrice(USD)", 11);
		mappings.put("seoTitle(en)", 12);
		mappings.put("seoTitle(fr)", 13);
		mappings.put("seoKeyWords(en)", 14);
		mappings.put("seoKeyWords(fr)", 15);
		mappings.put("seoDescription(en)", 16);
		mappings.put("seoDescription(fr)", 17);
		mappings.put("width", 18);
		mappings.put("length", 19);
		mappings.put("height", 20);
		mappings.put("weight", 21);
		mappings.put("A00996", 22);
		mappings.put("A00995(en)", 23);
		mappings.put("A00995(fr)", 24);
		mappings.put("A00601", 25);
		mappings.put("A00600", 26);
		mappings.put("A01006", 27);
		mappings.put("A04393(en)", 28);
		mappings.put("A04393(fr)", 29);
		mappings.put("A01278", 30);
		mappings.put("A01003(en)", 31);
		mappings.put("A01003(fr)", 32);
		mappings.put("A00430(en)", 33);
		mappings.put("A00430(fr)", 34);
		mappings.put("availabilityCriteria", 38);
		mappings.put("preBackOrderLimit", 39);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert Products"),
				"product_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Telescopes
		mappings.put("skuCode", 1);
		mappings.put("productCode", 2);
		mappings.put("description(en)", 3);
		mappings.put("description(fr)", 4);
		mappings.put("displayName(en)", 5);
		mappings.put("displayName(fr)", 6);
		mappings.put("image", 7);
		mappings.put("defaultCategoryCode", 8);
		mappings.put("brandCode", 9);
		mappings.put("listPrice(USD)", 10);
		mappings.put("salePrice(USD)", 11);
		mappings.put("seoTitle(en)", 12);
		mappings.put("seoTitle(fr)", 13);
		mappings.put("seoKeyWords(en)", 14);
		mappings.put("seoKeyWords(fr)", 15);
		mappings.put("seoDescription(en)", 16);
		mappings.put("seoDescription(fr)", 17);
		mappings.put("width", 18);
		mappings.put("length", 19);
		mappings.put("height", 20);
		mappings.put("weight", 21);
		mappings.put("A00996", 22);
		mappings.put("A00995(en)", 23);
		mappings.put("A00995(fr)", 24);
		mappings.put("A00601", 25);
		mappings.put("A00600", 26);
		mappings.put("A01006", 27);
		mappings.put("A04393(en)", 28);
		mappings.put("A04393(fr)", 29);
		mappings.put("A01278", 30);
		mappings.put("A01003(en)", 31);
		mappings.put("A01003(fr)", 32);
		mappings.put("A00430(en)", 33);
		mappings.put("A00430(fr)", 34);
		mappings.put("availabilityCriteria", 38);
		mappings.put("preBackOrderLimit", 39);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert and Update Product"),
				"product_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createUpdateProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Telescopes
		mappings.put("productCode", 2);
		mappings.put("description(en)", 3);
		mappings.put("description(fr)", 4);
		mappings.put("displayName(en)", 5);
		mappings.put("displayName(fr)", 6);
		mappings.put("seoDescription(en)", 16);
		mappings.put("seoDescription(fr)", 17);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Update Product"),
				"product_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createDeleteProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Delete Product"),
				"product_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertProductMultiCatalogImportJob(final Catalog catalog) {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Telescopes
		mappings.put("skuCode", 1);
		mappings.put("productCode", 2);
		mappings.put("description(en)", 3);
		mappings.put("description(fr)", 4);
		mappings.put("displayName(en)", 5);
		mappings.put("displayName(fr)", 6);
		mappings.put("image", 7);
		mappings.put("defaultCategoryCode", 8);
		mappings.put("brandCode", 9);
		mappings.put("listPrice(USD)", 10);
		mappings.put("salePrice(USD)", 11);
		mappings.put("seoTitle(en)", 12);
		mappings.put("seoTitle(fr)", 13);
		mappings.put("seoKeyWords(en)", 14);
		mappings.put("seoKeyWords(fr)", 15);
		mappings.put("seoDescription(en)", 16);
		mappings.put("seoDescription(fr)", 17);
		mappings.put("width", 18);
		mappings.put("length", 19);
		mappings.put("height", 20);
		mappings.put("weight", 21);
		mappings.put("A00996", 22);
		mappings.put("A00995(en)", 23);
		mappings.put("A00995(fr)", 24);
		mappings.put("A00601", 25);
		mappings.put("A00600", 26);
		mappings.put("A01006", 27);
		mappings.put("A04393(en)", 28);
		mappings.put("A04393(fr)", 29);
		mappings.put("A01278", 30);
		mappings.put("A01003(en)", 31);
		mappings.put("A01003(fr)", 32);
		mappings.put("A00430(en)", 33);
		mappings.put("A00430(fr)", 34);
		mappings.put("availabilityCriteria", 38);
		mappings.put("preBackOrderLimit", 39);

		ImportJob importJob = createSimpleImportJob(catalog, Utils.uniqueCode("Insert Products"),
				"product_multi_catalog_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateProductMultiCatalogImportJob(final Catalog catalog) {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Telescopes
		mappings.put("skuCode", 1);
		mappings.put("productCode", 2);
		mappings.put("description(en)", 3);
		mappings.put("description(fr)", 4);
		mappings.put("displayName(en)", 5);
		mappings.put("displayName(fr)", 6);
		mappings.put("image", 7);
		mappings.put("defaultCategoryCode", 8);
		mappings.put("brandCode", 9);
		mappings.put("listPrice(USD)", 10);
		mappings.put("salePrice(USD)", 11);
		mappings.put("seoTitle(en)", 12);
		mappings.put("seoTitle(fr)", 13);
		mappings.put("seoKeyWords(en)", 14);
		mappings.put("seoKeyWords(fr)", 15);
		mappings.put("seoDescription(en)", 16);
		mappings.put("seoDescription(fr)", 17);
		mappings.put("width", 18);
		mappings.put("length", 19);
		mappings.put("height", 20);
		mappings.put("weight", 21);
		mappings.put("A00996", 22);
		mappings.put("A00995(en)", 23);
		mappings.put("A00995(fr)", 24);
		mappings.put("A00601", 25);
		mappings.put("A00600", 26);
		mappings.put("A01006", 27);
		mappings.put("A04393(en)", 28);
		mappings.put("A04393(fr)", 29);
		mappings.put("A01278", 30);
		mappings.put("A01003(en)", 31);
		mappings.put("A01003(fr)", 32);
		mappings.put("A00430(en)", 33);
		mappings.put("A00430(fr)", 34);
		mappings.put("availabilityCriteria", 38);
		mappings.put("preBackOrderLimit", 39);

		ImportJob importJob = createSimpleImportJob(catalog, Utils.uniqueCode("Insert and Update Product"),
				"product_multi_catalog_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createUpdateProductMultiCatalogImportJob(final Catalog catalog) {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Telescopes
		mappings.put("productCode", 2);
		mappings.put("description(en)", 3);
		mappings.put("description(fr)", 4);
		mappings.put("displayName(en)", 5);
		mappings.put("displayName(fr)", 6);
		mappings.put("defaultCategoryCode", 8);
		mappings.put("seoDescription(en)", 16);
		mappings.put("seoDescription(fr)", 17);

		ImportJob importJob = createSimpleImportJob(catalog, Utils.uniqueCode("Update Product"),
				"product_multi_catalog_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createDeleteProductMultiCatalogImportJob(final Catalog catalog) {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);

		ImportJob importJob = createSimpleImportJob(catalog, Utils.uniqueCode("Delete Product"),
				"product_multi_catalog_delete.csv", AbstractImportTypeImpl.DELETE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.SINGLE_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertMultiskuProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Digital cameras (Multi-SKU)
		mappings.put("productCode", 1);
		mappings.put("description(en)", 2);
		mappings.put("description(fr)", 3);
		mappings.put("displayName(en)", 4);
		mappings.put("displayName(fr)", 5);
		mappings.put("image", 6);
		mappings.put("defaultCategoryCode", 7);
		mappings.put("brandCode", 8);
		mappings.put("listPrice(USD)", 9);
		mappings.put("salePrice(USD)", 10);
		mappings.put("seoTitle(en)", 11);
		mappings.put("seoTitle(fr)", 12);
		mappings.put("seoKeyWords(en)", 13);
		mappings.put("seoKeyWords(fr)", 14);
		mappings.put("seoDescription(en)", 15);
		mappings.put("seoDescription(fr)", 16);
		mappings.put("A00871(en)", 17);
		mappings.put("A00871(fr)", 18);
		mappings.put("A00870(en)", 19);
		mappings.put("A00870(fr)", 20);
		mappings.put("A00276(en)", 21);
		mappings.put("A00276(fr)", 22);
		mappings.put("A03378(en)", 23);
		mappings.put("A03378(fr)", 24);
		mappings.put("A03379", 25);
		mappings.put("A00341", 26);
		mappings.put("A03380", 27);
		mappings.put("A02028", 28);
		mappings.put("A01015(en)", 29);
		mappings.put("A01015(fr)", 30);
		mappings.put("A02805", 31);
		mappings.put("A03190", 32);
		mappings.put("A00140", 33);
		mappings.put("A01584(en)", 34);
		mappings.put("A01584(fr)", 35);
		mappings.put("A00152(en)", 36);
		mappings.put("A00152(fr)", 37);
		mappings.put("A00138(en)", 38);
		mappings.put("A00138(fr)", 39);
		mappings.put("A01206(en)", 40);
		mappings.put("A01206(fr)", 41);
		mappings.put("A00409(en)", 42);
		mappings.put("A00409(fr)", 43);
		mappings.put("A00413", 44);
		mappings.put("A00601", 45);
		mappings.put("A00600", 46);
		mappings.put("A00983", 47);
		mappings.put("A00985", 48);
		mappings.put("A00984", 49);
		mappings.put("A01071", 50);
		mappings.put("A00981(en)", 51);
		mappings.put("A00981(fr)", 52);
		mappings.put("A00373(en)", 53);
		mappings.put("A00373(fr)", 54);
		mappings.put("A03519", 55);
		mappings.put("A03517", 56);
		mappings.put("A01376", 57);
		mappings.put("A00551", 58);
		mappings.put("A03497", 59);
		mappings.put("A02638", 60);
		mappings.put("A01244(en)", 61);
		mappings.put("A01244(fr)", 62);
		mappings.put("A00430(en)", 63);
		mappings.put("A00430(fr)", 64);
		mappings.put("A00258", 65);
		mappings.put("A01381(en)", 66);
		mappings.put("A01381(fr)", 67);
		mappings.put("A00260", 68);
		mappings.put("A00652", 69);
		mappings.put("A00548", 70);
		mappings.put("A00919", 71);
		mappings.put("A01570(en)", 72);
		mappings.put("A01570(fr)", 73);
		mappings.put("A01207(en)", 74);
		mappings.put("A01207(fr)", 75);
		mappings.put("A00537", 76);
		mappings.put("A00256(en)", 77);
		mappings.put("A00256(fr)", 78);
		mappings.put("A00920(en)", 79);
		mappings.put("A00920(fr)", 80);
		mappings.put("A00556(en)", 81);
		mappings.put("A00556(fr)", 82);
		mappings.put("availabilityCriteria", 86);
		mappings.put("preBackOrderLimit", 87);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert MultiskuProduct"),
				"productmultisku_insert.csv", AbstractImportTypeImpl.INSERT_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.MULTI_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected ImportJob createInsertUpdateMultiskuProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		// Digital cameras (Multi-SKU)
		mappings.put("productCode", 1);
		mappings.put("description(en)", 2);
		mappings.put("description(fr)", 3);
		mappings.put("displayName(en)", 4);
		mappings.put("displayName(fr)", 5);
		mappings.put("image", 6);
		mappings.put("defaultCategoryCode", 7);
		mappings.put("brandCode", 8);
		mappings.put("listPrice(USD)", 9);
		mappings.put("salePrice(USD)", 10);
		mappings.put("seoTitle(en)", 11);
		mappings.put("seoTitle(fr)", 12);
		mappings.put("seoKeyWords(en)", 13);
		mappings.put("seoKeyWords(fr)", 14);
		mappings.put("seoDescription(en)", 15);
		mappings.put("seoDescription(fr)", 16);
		mappings.put("A00871(en)", 17);
		mappings.put("A00871(fr)", 18);
		mappings.put("A00870(en)", 19);
		mappings.put("A00870(fr)", 20);
		mappings.put("A00276(en)", 21);
		mappings.put("A00276(fr)", 22);
		mappings.put("A03378(en)", 23);
		mappings.put("A03378(fr)", 24);
		mappings.put("A03379", 25);
		mappings.put("A00341", 26);
		mappings.put("A03380", 27);
		mappings.put("A02028", 28);
		mappings.put("A01015(en)", 29);
		mappings.put("A01015(fr)", 30);
		mappings.put("A02805", 31);
		mappings.put("A03190", 32);
		mappings.put("A00140", 33);
		mappings.put("A01584(en)", 34);
		mappings.put("A01584(fr)", 35);
		mappings.put("A00152(en)", 36);
		mappings.put("A00152(fr)", 37);
		mappings.put("A00138(en)", 38);
		mappings.put("A00138(fr)", 39);
		mappings.put("A01206(en)", 40);
		mappings.put("A01206(fr)", 41);
		mappings.put("A00409(en)", 42);
		mappings.put("A00409(fr)", 43);
		mappings.put("A00413", 44);
		mappings.put("A00601", 45);
		mappings.put("A00600", 46);
		mappings.put("A00983", 47);
		mappings.put("A00985", 48);
		mappings.put("A00984", 49);
		mappings.put("A01071", 50);
		mappings.put("A00981(en)", 51);
		mappings.put("A00981(fr)", 52);
		mappings.put("A00373(en)", 53);
		mappings.put("A00373(fr)", 54);
		mappings.put("A03519", 55);
		mappings.put("A03517", 56);
		mappings.put("A01376", 57);
		mappings.put("A00551", 58);
		mappings.put("A03497", 59);
		mappings.put("A02638", 60);
		mappings.put("A01244(en)", 61);
		mappings.put("A01244(fr)", 62);
		mappings.put("A00430(en)", 63);
		mappings.put("A00430(fr)", 64);
		mappings.put("A00258", 65);
		mappings.put("A01381(en)", 66);
		mappings.put("A01381(fr)", 67);
		mappings.put("A00260", 68);
		mappings.put("A00652", 69);
		mappings.put("A00548", 70);
		mappings.put("A00919", 71);
		mappings.put("A01570(en)", 72);
		mappings.put("A01570(fr)", 73);
		mappings.put("A01207(en)", 74);
		mappings.put("A01207(fr)", 75);
		mappings.put("A00537", 76);
		mappings.put("A00256(en)", 77);
		mappings.put("A00256(fr)", 78);
		mappings.put("A00920(en)", 79);
		mappings.put("A00920(fr)", 80);
		mappings.put("A00556(en)", 81);
		mappings.put("A00556(fr)", 82);
		mappings.put("availabilityCriteria", 86);
		mappings.put("preBackOrderLimit", 87);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert and Update MultiskuProduct"),
				"productmultisku_insert_update.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.MULTI_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	/**
	 *
	 * @return ImportJob instance
	 */
	protected ImportJob createUpdateMultiskuProductImportJob() {
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("productCode", 1);
		mappings.put("description(en)", 2);
		mappings.put("description(fr)", 3);
		mappings.put("displayName(en)", 4);
		mappings.put("displayName(fr)", 5);
		mappings.put("seoDescription(en)", 15);
		mappings.put("seoDescription(fr)", 16);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Update MultiskuProduct"),
				"productmultisku_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, PRODUCT_PREFIX
						+ ImportJobScenario.MULTI_SKU_PRODUCT_TYPE, mappings);
		return importJob;
	}

	protected Date string2Date(final String dateString) throws ParseException {
		return new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy").parse(dateString);
	}
}
