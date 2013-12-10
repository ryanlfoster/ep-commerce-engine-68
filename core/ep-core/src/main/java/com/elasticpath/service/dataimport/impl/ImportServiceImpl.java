/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.dataimport.ImportJobExistException;
import com.elasticpath.service.dataimport.ImportJobRunner;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;

/**
 * A default implementation of <code>ImportService</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveClassLength" })
public class ImportServiceImpl extends AbstractEpPersistenceServiceImpl implements ImportService {

	private static final String PLACEHOLDER_FOR_LIST = "list";
	/**
	 * The logger.
	 */
	protected static final Logger LOG = Logger.getLogger(ImportServiceImpl.class);

	private AssetRepository assetRepository;
	
	private CatalogService catalogService;
	
	private WarehouseService warehouseService;
	
	private StoreService storeService;
	
	private ImportJobStatusHandler importJobStatusHandler;
	
	private ImportNotificationDao importNotificationDao;
	
	private TimeService timeService;

	/**
	 * List all saved import jobs.
	 * 
	 * @return a list of saved import jobs.
	 */
	public List<ImportJob> listImportJobs() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("IMPORT_JOB_SELECT_ALL");
	}

	/**
	 * List all saved import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @return a list of saved import jobs.
	 */
	public List<ImportJob> listImportJobs(final int startIndex, final int maxResults) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("IMPORT_JOB_SELECT_ALL", startIndex, maxResults);
	}

	/**
	 * List all catalog import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @param catalogUids limit the import jobs to the ones for the catalogs listed.
	 *                     Listing none will return all catalog import jobs.
	 * @return a list of saved import jobs.
	 */
	public List<ImportJob> listCatalogImportJobs(final int startIndex, final int maxResults, final Long ... catalogUids) {
		List<ImportJob> catalogJobs = listCatalogImportJobs();
		restrictToSpecifiedCatalogs(catalogUids, catalogJobs);
		return limitToSinglePage(startIndex, maxResults, catalogJobs);
	}


	private void restrictToSpecifiedCatalogs(final Long[] catalogUids,
			final List<ImportJob> catalogJobs) {
		if (catalogUids == null  || catalogUids.length == 0) {
			return;
		}
		Set<Long> catalogIdsSet = new HashSet<Long>(Arrays.asList(catalogUids));
		for (Iterator<ImportJob> iter = catalogJobs.iterator(); iter.hasNext();) {
			if (!catalogIdsSet.contains(iter.next().getCatalog().getUidPk())) {
				iter.remove();
			}
		}
	}

	private List<ImportJob> limitToSinglePage(final int startIndex,
			final int maxResults, final List<ImportJob> jobs) {
		
		List<ImportJob> resultList = new ArrayList<ImportJob>();
		if (jobs.size() >= (startIndex + maxResults)) {
			resultList.addAll(jobs.subList(startIndex, startIndex + maxResults));
		} else if (jobs.size() > startIndex) {
			resultList.addAll(jobs.subList(startIndex, jobs.size()));
		}
		return resultList;
	}

	/**
	 * Gets the count of catalog jobs stored in the database.
	 * 
	 * @param catalogUids (optional) restrict the count the jobs in the specified catalogs
	 * @return the number of ImportJob stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	public long countCatalogJobs(final Long ... catalogUids) {
		List<ImportJob> jobs = listCatalogImportJobs();
		restrictToSpecifiedCatalogs(catalogUids, jobs);
		return jobs.size();
	}

	/**
	 * @return the list of all catalog import jobs.
	 */
	List<ImportJob> listCatalogImportJobs() {
		Map<String, ImportDataType> catalogTypes = new HashMap<String, ImportDataType>();
		for (final long catalogUid : listCatalogUids()) {
			catalogTypes.putAll(retrieveCatalogImportDataTypes(catalogUid));
		}
		return filterImportJobsByType(catalogTypes);
	}

	/**
	 * List all customer import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @param storeUids limit the import jobs to the ones for the stores listed.
	 *                   Listing none will return all customer import jobs.
	 * @return a list of saved import jobs.
	 */
	public List<ImportJob> listCustomerImportJobs(final int startIndex, final int maxResults, final Long ... storeUids) {
		List<ImportJob> customerJobs = listCustomerImportJobs();
		restrictToSpecifiedStores(storeUids, customerJobs);
		return limitToSinglePage(startIndex, maxResults, customerJobs);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Avoids transferring jobs to the client side.
	 * @param priceListGuids optional filter price list jobs by available to user price lists
	 */
	public long countPriceListImportJobs(final String ... priceListGuids) {
		return listPriceListImportJobs(priceListGuids).size();
	}
	
	/**
	 * List all saved import jobs that are dependent on PriceListDescriptors.
	 * @param priceListGuids optional filter price list jobs by available to user price lists
	 * @return the list of saved jobs
	 */
	public List<ImportJob> listPriceListImportJobs(final String ... priceListGuids) {
		List<ImportJob> resultList = new ArrayList<ImportJob>();
		Map<String, ImportDataType> priceListTypes = retrievePriceListImportDataTypes();
		for (ImportJob job : listImportJobs()) {
			if (priceListTypes.containsKey(job.getImportDataTypeName())) {
				resultList.add(job);
			}
		}
		restrictToSpecifiedPriceLists(priceListGuids, resultList);
		return resultList; 		
	}
	
	private void restrictToSpecifiedPriceLists(final String[] priceListGuids,
			final List<ImportJob> jobs) {
		if (priceListGuids != null && priceListGuids.length > 0) {
			final Set<String> priceListGuidsSet = new HashSet<String>(Arrays.asList(priceListGuids));
			final List<ImportJob> jobsToRetain = new ArrayList<ImportJob>();
			for (ImportJob job : jobs) {
				if (priceListGuidsSet.contains(job.getDependentPriceListGuid())) {
					jobsToRetain.add(job);					
				}
			}
			jobs.retainAll(jobsToRetain);
		}
	}
	

	/**
	 * @return a list of all customer import jobs.
	 */
	List<ImportJob> listCustomerImportJobs() {
		return filterImportJobsByType(retrieveCustomerImportDataTypes());
	}
	
	private void restrictToSpecifiedStores(final Long[] storeUids,
			final List<ImportJob> customerJobs) {
		if (storeUids == null  || storeUids.length == 0) {
			return;
		}
		Set<Long> uidSet = new HashSet<Long>(Arrays.asList(storeUids));
		for (Iterator<ImportJob> iter = customerJobs.iterator(); iter.hasNext();) {
			ImportJob job = iter.next();
			if (job.getStore() == null || !uidSet.contains(job.getStore().getUidPk())) {
				iter.remove();
			}
		}
	}

	/**
	 * Gets the count of jobs stored in the database.
	 * 
	 * @param storeUids (optional) restrict the count the jobs in the specified stores.
	 * @return the number of jobs stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	public long countCustomerJobs(final Long ... storeUids) {
		List<ImportJob> customerJobs = listCustomerImportJobs();
		restrictToSpecifiedStores(storeUids, customerJobs);
		return customerJobs.size();
	}

	/**
	 * List all warehouse import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @param warehouseUids limit the import jobs to the ones for the warehouse listed.
	 *                     Listing none will return all warehouse import jobs.
	 * @return a list of saved import jobs.
	 */
	public List<ImportJob> listWarehouseImportJobs(final int startIndex, final int maxResults, final Long ... warehouseUids) {
		List<ImportJob> warehouseJobs = listWarehouseImportJobs();
		restrictToSpecifiedWarehouses(warehouseUids, warehouseJobs);
		return limitToSinglePage(startIndex, maxResults, warehouseJobs);
	}
	
	private void restrictToSpecifiedWarehouses(final Long[] warehouseUids,
			final List<ImportJob> warehouseJobs) {
		if (warehouseUids == null  || warehouseUids.length == 0) {
			return;
		}
		Set<Long> uidSet = new HashSet<Long>(Arrays.asList(warehouseUids));
		for (Iterator<ImportJob> iter = warehouseJobs.iterator(); iter.hasNext();) {
			ImportJob job = iter.next();
			if (job.getWarehouse() == null || !uidSet.contains(job.getWarehouse().getUidPk())) {
				iter.remove();
			}
		}
	}

	/**
	 * Gets the count of warehouse jobs stored in the database.
	 * 
	 * @param warehouseUids (optional) restrict the count the jobs in the specified warehouses.
	 * @return the number of ImportJob stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	public long countWarehouseJobs(final Long ... warehouseUids) {
		List<ImportJob> jobs = listWarehouseImportJobs();
		restrictToSpecifiedWarehouses(warehouseUids, jobs);
		return jobs.size();
	}

	/**
	 * @return all the warehouse import jobs
	 */
	List<ImportJob> listWarehouseImportJobs() {
		return filterImportJobsByType(retrieveWarehouseImportDataTypes());
	}

	private List<ImportJob> filterImportJobsByType(
			final Map<String, ImportDataType> typesToRetain) {
		List<ImportJob> allJobs = listImportJobs();
		List<ImportJob> resultList = new ArrayList<ImportJob>();
		for (ImportJob job : allJobs) {
			if (typesToRetain.get(job.getImportDataTypeName()) != null) {
				resultList.add(job);
			}
		}
		return resultList;
	}

	/**
	 * List all catalogs.
	 * 
	 * @return a list of catalogs.
	 * @deprecated Use the catalog service wherever possible
	 */
	 @Deprecated
	public List<Catalog> listCatalogs() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_CATALOGS");
	}
	
	private List<Long> listCatalogUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_CATALOG_UIDS");
	}

	/**
	 * Find a catalog by name.
	 * 
	 * @param name the catalog name
	 * @return the catalog with the given name if it exist, otherwise, <code>null</code>.
	 * @deprecated Use the CatalogService directly for this operation where possible
	 */
	@Deprecated
	public Catalog findCatalog(final String name) {
		return catalogService.findByName(name);
	}

	/**
	 * List all stores.
	 * 
	 * @return a list of stores.
	 * @deprecated use the StoreService directly for this operation where possible
	 */
	@Deprecated
	public List<Store> listStores() {
		return storeService.findAllStores();
	}

	/**
	 * Find a store by name.
	 * 
	 * @param name the store name
	 * @return the store with the given name if it exist, otherwise, <code>null</code>.
	 */
	public Store findStore(final String name) {
		final List<Store> stores = getPersistenceEngine().retrieveByNamedQuery("FIND_STORE_BY_NAME", name);

		final int size = stores.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new EpServiceException("Inconsistent Data -- duplicate store names exist :" + name);
		}

		return stores.get(0);
	}
	
	/**
	 * List all warehouses.
	 * 
	 * @return a list of warehouses.
	 * @deprecated use the WarehouseSerice directly for this operation where possible
	 */
	@Deprecated
	public List<Warehouse> listWarehouses() {
		return warehouseService.findAllWarehouses();
	}

	/**
	 * Find a warehouse by name.
	 * 
	 * @param name the warehouse name
	 * @return the warehouse with the given name if it exist, otherwise, <code>null</code>.
	 */
	public Warehouse findWarehouse(final String name) {
		final List<Warehouse> warehouses = getPersistenceEngine().retrieveByNamedQuery("FIND_WAREHOUSE_BY_NAME", name);

		final int size = warehouses.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new EpServiceException("Inconsistent Data -- duplicate warehouse names exist :" + name);
		}

		return warehouses.get(0);
	}

	/**
	 * List all import data types.
	 * 
	 * @return a list of import data types
	 */
	public List<ImportDataType> listImportDataTypes() {
		final Map<String, ImportDataType> importDataTypes = retrieveImportDataTypes();
		return new ArrayList<ImportDataType>(importDataTypes.values());
	}

	/**
	 * List the {@code ImportDataType}s available for the catalog with the given UID.
	 * 
	 * @param catalogUid the catalog UID
	 * @return a list of catalog import data types
	 */
	public List<ImportDataType> getCatalogImportDataTypes(final long catalogUid) {
		final Map<String, ImportDataType> importDataTypes = retrieveCatalogImportDataTypes(catalogUid);
		return new ArrayList<ImportDataType>(importDataTypes.values());
	}

	/**
	 * List all customer import data types.
	 * 
	 * @return a list of customer import data types
	 */
	public List<ImportDataType> getCustomerImportDataTypes() {
		final Map<String, ImportDataType> importDataTypes = retrieveCustomerImportDataTypes();
		return new ArrayList<ImportDataType>(importDataTypes.values());
	}

	/**
	 * List all warehouse import data types.
	 * 
	 * @return a list of warehouse import data types
	 */
	public List<ImportDataType> getWarehouseImportDataTypes() {
		final Map<String, ImportDataType> importDataTypes = retrieveWarehouseImportDataTypes();
		return new ArrayList<ImportDataType>(importDataTypes.values());
	}

	@Override
	public List<ImportDataType> getPriceListImportDataTypes() {
		final Map<String, ImportDataType> importDataTypes = retrievePriceListImportDataTypes();
		return new ArrayList<ImportDataType>(importDataTypes.values());
	}
	
	private Map<String, ImportDataType> retrieveImportDataTypes() {
		Map<String, ImportDataType> importDataTypes = new TreeMap<String, ImportDataType>();
		for (final long catalogUid : listCatalogUids()) {
			importDataTypes.putAll(retrieveCatalogImportDataTypes(catalogUid));
		}
		importDataTypes.putAll(retrieveCustomerImportDataTypes());
		importDataTypes.putAll(retrieveWarehouseImportDataTypes());
		retrieveBaseAmountImportDataType(importDataTypes);
		retrieveCouponImportDataType(importDataTypes);
		return importDataTypes;
	}

	private Map<String, ImportDataType> retrieveCatalogImportDataTypes(final long catalogUid) {
		Map<String, ImportDataType> importDataTypes = new TreeMap<String, ImportDataType>();
		if (isMasterCatalog(catalogUid)) {
			this.retrieveCategoryImportDataTypes(importDataTypes, catalogUid);
			this.retrieveProductImportDataTypes(importDataTypes, catalogUid);
			this.retrieveProductSkuImportDataTypes(importDataTypes, catalogUid);
			this.retrieveProductCategoryAssociationImportDataType(importDataTypes);
		}
		this.retrieveProductAssociationImportDataType(importDataTypes);
		return importDataTypes;
	}
	
	/**
	 * @return a map of all the {@link ImportDataType} classes that are related to PriceList imports,
	 * keyed on the {@code ImportDataType}'s Name field.
	 */
	Map<String, ImportDataType> retrievePriceListImportDataTypes() {
		Map<String, ImportDataType> importDataTypes = new TreeMap<String, ImportDataType>();
		retrieveBaseAmountImportDataType(importDataTypes);
		return importDataTypes;
	}

	/**
	 * Checks whether the given UID belongs to a master catalog.
	 * 
	 * @param catalogUid the catalog UID
	 * @return true if the catalog is master
	 */
	protected boolean isMasterCatalog(final long catalogUid) {
		List<Catalog> catalogs = getPersistenceEngine().retrieveByNamedQuery("FIND_CATALOG_BY_UID", catalogUid);
		return catalogs.iterator().next().isMaster();
	}

	private Map<String, ImportDataType> retrieveCustomerImportDataTypes() {
		Map<String, ImportDataType> importDataTypes = new TreeMap<String, ImportDataType>();
		this.retrieveCustomerImportDataType(importDataTypes);
		this.retrieveCustomerAddressImportDataType(importDataTypes);
		return importDataTypes;
	}

	private Map<String, ImportDataType> retrieveWarehouseImportDataTypes() {
		Map<String, ImportDataType> importDataTypes = new TreeMap<String, ImportDataType>();
		this.retrieveInventoryImportDataType(importDataTypes);
		return importDataTypes;
	}

	private void retrieveCustomerImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_CUSTOMER);
		importDataType.init(null);
		importDataTypes.put(importDataType.getName(), importDataType);
	}

	private void retrieveCustomerAddressImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_CUSTOMER_ADDRESS);
		importDataType.init(null);
		importDataTypes.put(importDataType.getName(), importDataType);
	}

	private void retrieveProductAssociationImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_ASSOCIATION);
		importDataType.init(null);
		importDataTypes.put(importDataType.getName(), importDataType);
	}

	private void retrieveProductCategoryAssociationImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataType =
				getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_CATEGORY_ASSOCIATION);
		importDataType.init(null);
		importDataTypes.put(importDataType.getName(), importDataType);

	}

	private void retrieveInventoryImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_INVENTORY);
		importDataType.init(null);
		importDataTypes.put(importDataType.getName(), importDataType);
	}
	
	private void retrieveBaseAmountImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_BASEAMOUNT);
		importDataType.init(null);
		importDataTypes.put(importDataType.getName(), importDataType);
	}
	
	private void retrieveCouponImportDataType(final Map<String, ImportDataType> importDataTypes) {
		final ImportDataType importDataTypeCouponCode = getBean(ContextIdNames.IMPORT_DATA_TYPE_COUPONCODE);
		importDataTypeCouponCode.init(null);
		importDataTypes.put(importDataTypeCouponCode.getName(), importDataTypeCouponCode);
		
		final ImportDataType importDataTypeCouponCodeEmail = getBean(ContextIdNames.IMPORT_DATA_TYPE_COUPONCODE_EMAIL);
		importDataTypeCouponCodeEmail.init(null);
		importDataTypes.put(importDataTypeCouponCodeEmail.getName(), importDataTypeCouponCodeEmail);
	}

	private void retrieveProductImportDataTypes(final Map<String, ImportDataType> importDataTypes, final long catalogUid) {
		final List<ProductType> productTypes = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_BY_CATALOG_EAGER", catalogUid);

		for (ProductType productType : productTypes) {
			final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT);
			importDataType.init(productType);
			importDataTypes.put(importDataType.getName(), importDataType);
		}
	}

	private void retrieveCategoryImportDataTypes(final Map<String, ImportDataType> importDataTypes, final long catalogUid) {
		final List<CategoryType> categoryTypes = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPE_BY_CATALOG_EAGER", catalogUid);

		for (final CategoryType categoryType : categoryTypes) {
			// getPersistenceEngine().initialize(categoryType.getAttributeGroup().getAttributeGroupAttributes());

			final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_CATEGORY);
			importDataType.init(categoryType);
			importDataTypes.put(importDataType.getName(), importDataType);
		}
	}

	private void retrieveProductSkuImportDataTypes(final Map<String, ImportDataType> importDataTypes, final long catalogUid) {
		final List<ProductType> productTypes = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_BY_CATALOG_EAGER", catalogUid);

		for (final ProductType productType : productTypes) {
			if (productType.isWithMultipleSkus()) {
				// getPersistenceEngine().initialize(productType.getSkuAttributeGroup().getAttributeGroupAttributes());

				final ImportDataType importDataType = getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_SKU);
				importDataType.init(productType);
				importDataTypes.put(importDataType.getName(), importDataType);
			}
		}

	}

	/**
	 * Find an import job by name.
	 * 
	 * @param name the import job name
	 * @return the import job of the given name if it exist, otherwise, <code>null</code>.
	 */
	public ImportJob findImportJob(final String name) {
		final List<ImportJob> importJobs = getPersistenceEngine().retrieveByNamedQuery("IMPORT_JOB_FIND_BY_NAME", name);

		final int size = importJobs.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new EpServiceException("Inconsistent Data -- duplicate import job names exist :" + name);
		}

		return importJobs.get(0);
	}

	/**
	 * Find an import data type by name.
	 * 
	 * @param name the import data type name
	 * @return the import data type of the given name if it exist, otherwise, <code>null</code>.
	 */
	public ImportDataType findImportDataType(final String name) {
		final Map<String, ImportDataType> importDataTypes = retrieveImportDataTypes();
		return importDataTypes.get(name);
	}
	
	/**
	 * Initialize locales for importDataType.
	 *
	 * @param importDataType import data type
	 * @param importJob for retrieving catalog/store and getting its locales
	 * @return ImportDataType with changed locales
	 */
	public ImportDataType initImportDataTypeLocalesAndCurrencies(final ImportDataType importDataType, final ImportJob importJob) {
		if (importJob.getCatalog() != null) {
			final PriceListHelperService priceListHelperService = getBean(ContextIdNames.PRICE_LIST_HELPER_SERVICE);
			importDataType.setSupportedLocales(importJob.getCatalog().getSupportedLocales());
			importDataType.setSupportedCurrencies(priceListHelperService.getAllCurrenciesFor((importJob.getCatalog())));
			importDataType.setRequiredCurrency(getRequiredCurrency(importJob, priceListHelperService));
			importDataType.setRequiredLocale(importJob.getCatalog().getDefaultLocale());
		} 
		if (importJob.getStore() != null) {
			importDataType.setSupportedLocales(importJob.getStore().getSupportedLocales());
			importDataType.setSupportedCurrencies(importJob.getStore().getSupportedCurrencies());
			importDataType.setRequiredCurrency(importJob.getStore().getDefaultCurrency());
			importDataType.setRequiredLocale(importJob.getStore().getDefaultLocale());
		}
		return importDataType;
	}

	/**
	 * Gets required currency from the catalog.
	 * 
	 * @param importJob the import job
	 * @param priceListHelperService ptrice list helper service
	 * @return required currency if the catalog has PLAs, otherwise - null
	 */
	protected Currency getRequiredCurrency(final ImportJob importJob,
			final PriceListHelperService priceListHelperService) {
		Iterator<Currency> iter = priceListHelperService.getAllCurrenciesFor(importJob.getCatalog()).iterator();
		Currency requiredCurrency = null;
		if (iter.hasNext()) {
			requiredCurrency = iter.next();
		}
		return requiredCurrency;
	}

	/**
	 * Returns an import job.
	 * 
	 * @param uid the persistent instance uid
	 * @return the persistent instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return null;
	}

	/**
	 * Get the import job with the given UID. Return null if no matching record exists.
	 * 
	 * @param importJobUid the importJob UID, give 0 if you want to create a new import job.
	 * @return the importJob if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ImportJob getImportJob(final long importJobUid) throws EpServiceException {
		sanityCheck();
		ImportJob importJob = null;
		if (importJobUid <= 0) {
			importJob = getBean(ContextIdNames.IMPORT_JOB);
		} else {
			importJob = getPersistentBeanFinder().get(ContextIdNames.IMPORT_JOB, importJobUid);
		}
		return importJob;
	}

	/**
	 * Validate the csv format of the import data file.
	 * 
	 * @param importJobRequest the import job request
	 * @return a list of lines that not complying with the csv format. If all lines are good, then returns an empty list.
	 */
	public List<ImportBadRow> validateCsvFormat(final ImportJobRequest importJobRequest) {
		validateImportJobRequest(importJobRequest);
		final CsvFileReader csvFileReader = getBean(ContextIdNames.CSV_FILE_READER);
		csvFileReader
				.open(getRemoteCsvFileName(importJobRequest.getImportSource()), 
						importJobRequest.getImportSourceColDelimiter(), 
						importJobRequest.getImportSourceTextQualifier());

		final List<ImportBadRow> importBadRows = new ArrayList<ImportBadRow>();

		// Get the number of fields of the title line
		final String[] titleLine = csvFileReader.readNext();
		validateTitleLine(titleLine, importBadRows, importJobRequest);
		final Integer fieldsNumberOfTitleLine = Integer.valueOf(titleLine.length - 1);

		int rowNumber = 1;
		String[] nextLine;
		while ((nextLine = csvFileReader.readNext()) != null) {
			rowNumber++;
			final int fieldsNumber = nextLine.length - 1;
			if (fieldsNumber != fieldsNumberOfTitleLine.intValue()) {
				final ImportBadRow importBadRow = getBean(ContextIdNames.IMPORT_BAD_ROW);
				importBadRow.setRowNumber(rowNumber);
				importBadRow.setRow(nextLine[0]);

				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badRow.wrongColumns");
				importFault.setArgs(new String[] { fieldsNumberOfTitleLine.toString(), String.valueOf(fieldsNumber) });

				importBadRow.addImportFault(importFault);
				importBadRows.add(importBadRow);
			}
		}

		csvFileReader.close();
		return importBadRows;
	}

	/**
	 * Verifies an import job request for its required fields.
	 * 
	 * @param request the request
	 */
	protected void validateImportJobRequest(final ImportJobRequest request) {
		if (request.getImportJob() == null) {
			throw new IllegalArgumentException("The request's import job field is required.");
		}
		if (request.getInitiator() == null) {
			throw new IllegalArgumentException("The request's initiator field is required.");
		}
		if (request.getRequestId() == null) {
			throw new IllegalArgumentException("No request ID has been provided with the request.");
		}
	}

	/**
	 * Validates the header line.
	 * 
	 * Default access for unit testing.
	 * @param titleRow The row to validate.
	 * @param importBadRows The list to add any errors to.
	 * @param importJobRequest The job request.
	 */
	void validateTitleLine(final String[] titleRow, final List<ImportBadRow> importBadRows, final ImportJobRequest importJobRequest) {
		final List<ImportFault> faults = new ArrayList<ImportFault>();

		// We allow hyphens in the headers for base amounts
		boolean isBaseAmountImport = "Base Amount".equals(importJobRequest.getImportJob().getImportDataTypeName());
		
		for (int i = 1; i < titleRow.length; i++) {
			if (titleRow[i].equals("")) {
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.emptyTitleNameNotAllow");
				importFault.setArgs(new Object[] { String.valueOf(i) });
				faults.add(importFault);
			} else if (titleRow[i].indexOf('\n') > 0) {
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.titleColumnCannotWrapMultipleLines");
				importFault.setArgs(new Object[] { String.valueOf(i) });
				faults.add(importFault);
			} else if ((isBaseAmountImport && !titleRow[i].matches("^[-_ \\p{Alnum}]+$"))
					|| (!isBaseAmountImport && !titleRow[i].matches("^[_ \\p{Alnum}]+$"))) {
				// Title name only allow the following characters: digital, alphabetic, space and '_'.
				// Other characters, like '"', may break the import mapping page.
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badTitleName");
				importFault.setArgs(new Object[] { String.valueOf(i) });
				faults.add(importFault);
			}
		}

		if (!faults.isEmpty()) {
			final ImportBadRow importBadRow = getBean(ContextIdNames.IMPORT_BAD_ROW);
			importBadRow.setRowNumber(0);
			importBadRow.setRow(titleRow[0]);
			importBadRow.addImportFaults(faults);
			importBadRows.add(importBadRow);
		}
	}

	/**
	 * Returns the preview data of the import data file.
	 * 
	 * @param importJob the import job
	 * @param maxPreviewRows maximum rows for preview data, that will be imported.
	 * @param returnRawData the boolean value to determine if the result need to be filtered. True will return the raw data.
	 *  
	 * @return a list of records for previewing
	 */
	public List<List<String>> getPreviewData(final ImportJob importJob, final int maxPreviewRows, final boolean returnRawData) {
		final CsvFileReader csvFileReader = getBean(ContextIdNames.CSV_FILE_READER);
		csvFileReader.open(getRemoteCsvFileName(importJob.getCsvFileName()), 
				importJob.getCsvFileColDelimeter(), importJob.getCsvFileTextQualifier());
		final List<String[]> previewDataWithOriginalLine = csvFileReader.getTopLines(maxPreviewRows);
		csvFileReader.close();

		final List<List<String>> previewData = new ArrayList<List<String>>();
		for (String[] row : previewDataWithOriginalLine) {
			previewData.add(new ArrayList<String>(Arrays.asList(row).subList(1, row.length)));
		}

		if (returnRawData) {
			return previewData;
		}
		return filterMappedDataForPreview(previewData, importJob);
	}
	
	/**
	 * Returns the preview data of the import data file.
	 * 
	 * @param importJob the import job
	 * @param maxPreviewRows maximum rows for preview data, that will be imported.
	 *  
	 * @return a list of records for previewing
	 */
	public List<List<String>> getPreviewData(final ImportJob importJob, final int maxPreviewRows) {
		return getPreviewData(importJob, maxPreviewRows, false);
	}
	
	/**
	 * Removes unmapped columns from preview data.
	 *
	 * @param previewData unfiltered preview data
	 * @param importJob the import job
	 * @return a list of records for previewing. Contains only fields mapped in import job)
	 */
	private List<List<String>> filterMappedDataForPreview(final List<List<String>> previewData, final ImportJob importJob) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		for (int i = 0; i < previewData.size(); ++i) {
			result.add(new ArrayList<String>());
		}

		// need to sort for correct order
		List<Integer> listOfMappingValues = new ArrayList<Integer>(importJob.getMappings().values());
		Collections.sort(listOfMappingValues);
		
		for (int column : listOfMappingValues) {
			for (int row = 0; row < result.size(); ++row) {
				if (column > 0) {
					final List<String> previewDataRow = previewData.get(row);
					if (column <= previewDataRow.size()) {
						result.get(row).add(previewDataRow.get(column - 1));
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public List<ImportBadRow> validateMappings(final ImportJobRequest importJobRequest) {
		validateImportJobRequest(importJobRequest);
		
		// update the CSV file path with the local file path
		importJobRequest.setImportSource(getRemoteCsvFileName(importJobRequest.getImportSource()));
		
		final ImportDataType importDataType = findImportDataType(importJobRequest.getImportJob().getImportDataTypeName());
		ImportJobRunner importJobRunner = getBean(importDataType.getImportJobRunnerBeanName());
		importJobRunner.init(importJobRequest, importJobRequest.getRequestId());

		return importJobRunner.validate(importJobRequest.getReportingLocale());
	}

	/**
	 * Save or update the given import job.
	 * 
	 * @param importJob the importJob to save or update
	 * @return the updated instance of the importJob
	 * @throws ImportJobExistException - if the specified import job name is already in use.
	 * @see ImportJob
	 */
	public ImportJob saveOrUpdateImportJob(final ImportJob importJob) throws ImportJobExistException {
		final ImportJob result = findImportJob(importJob.getName());
		// check whether job name is already used, if it is, throw ImportJobExistException
		if (result != null && (importJob.getUidPk() == 0 || importJob.getUidPk() != result.getUidPk())) {
			throw new ImportJobExistException("Import job name already exists");
		}
		sanityCheck();

		return getPersistenceEngine().merge(importJob);
	}

	@Override
	public ImportJobStatus scheduleImport(final ImportJobRequest importJobRequest) {
		validateImportJobRequest(importJobRequest);
		
		ImportJob importJob = importJobRequest.getImportJob();
		CmUser initiator = importJobRequest.getInitiator();
		
		ImportNotification notification = getBean(ContextIdNames.IMPORT_NOTIFICATION);
		notification.setAction(ImportAction.LAUNCH_IMPORT);
		notification.setImportJob(importJob);
		notification.setImportSource(importJobRequest.getImportSource());
		notification.setInitiator(initiator);
		notification.setReportingLocale(importJobRequest.getReportingLocale());
		notification.setMaxAllowedFailedRows(importJobRequest.getMaxAllowedFailedRows());
		notification.setProcessId(importJobRequest.getRequestId());
		notification.setImportSourceColDelimiter(importJobRequest.getImportSourceColDelimiter());
		notification.setImportSourceTextQualifier(importJobRequest.getImportSourceTextQualifier());
		notification.setImportType(importJobRequest.getImportType());
		notification.setChangeSetGuid(importJobRequest.getChangeSetGuid());
		notification.setParameter(importJobRequest.getParameter());
		
		// set the date created using the time provided by the TimeService
		notification.setDateCreated(timeService.getCurrentTime());
		
		notification = importNotificationDao.add(notification);
		
		return importJobStatusHandler.initialiseJobStatus(notification.getProcessId(), importJob, initiator);
	}
	
	@Override
	public ImportJobStatus getImportJobStatus(final String importJobProcessGuid) {
		return importJobStatusHandler.getImportJobStatus(importJobProcessGuid);
	}
	
	/**
	 * The method is made protected in order to give subclasses opportunity to alter remote file name.
	 * 
	 * @param csvFileName the file name
	 * @return remote file name
	 */
	protected String getRemoteCsvFileName(final String csvFileName) {
		File csvFile = new File(csvFileName.replace('\\', '/'));
		final StringBuffer csvFileNameFullPath = new StringBuffer();
		csvFileNameFullPath.append(getImportAssetPath());
		csvFileNameFullPath.append(File.separator);
		csvFileNameFullPath.append(csvFile.getName());
		return csvFileNameFullPath.toString();
	}
	
	/**
	 * This implementation uses {@link AssetRepository}.
	 * @return the full path to the import assets directory
	 */
	String getImportAssetPath() {
		return getAssetRepository().getImportAssetPath();
	}
	
	/**
	 * Returns the title line of the import csv data file.
	 * 
	 * @param importJobRequest the import job request
	 * @return a list of fields of the title line
	 */
	public List<String> getTitleLine(final ImportJobRequest importJobRequest) {
		validateImportJobRequest(importJobRequest);
		
		String localFile = getRemoteCsvFileName(importJobRequest.getImportSource());
		final CsvFileReader csvFileReader = getBean(ContextIdNames.CSV_FILE_READER);
		csvFileReader.open(localFile, 
				importJobRequest.getImportSourceColDelimiter(), 
				importJobRequest.getImportSourceTextQualifier());
		String[] titleLine = csvFileReader.readNext();
		csvFileReader.close();
		return new ArrayList<String>(Arrays.asList(titleLine).subList(1, titleLine.length));
	}

	/**
	 * List all import types.
	 * 
	 * @return a list of import types.
	 */
	public List<ImportType> listImportTypes() {
		return AbstractImportTypeImpl.getAllImportTypes();
	}

	/**
	 * Returns the import type with the given id.
	 * 
	 * @param importTypeId the import type id
	 * @return the import type with the given id
	 */
	public ImportType getImportType(final int importTypeId) {
		return AbstractImportTypeImpl.getInstance(importTypeId);
	}

	/**
	 * Creates and returns an import fault error.
	 * 
	 * @return an import fault error
	 */
	protected ImportFault getImportFaultError() {
		final ImportFault importFault = getBean(ContextIdNames.IMPORT_FAULT);
		importFault.setLevel(ImportFault.ERROR);
		return importFault;
	}

	/**
	 * Delete the importJob.
	 * 
	 * @param importJob the job to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final ImportJob importJob) {
		sanityCheck();
		getPersistenceEngine().executeNamedQuery("DELETE_IMPORT_JOB_STATUS_BY_JOB_GUID", importJob.getGuid());
		getPersistenceEngine().executeNamedQuery("DELETE_IMPORT_NOTIFICATIONS_BY_JOB_GUID", importJob.getGuid());
		getPersistenceEngine().delete(importJob);
	}

	/**
	 * Gets the count of jobs stored in the database.
	 * 
	 * @return the number of ImportJob stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	public long count() {
		sanityCheck();
		List< ? > countList = getPersistenceEngine().retrieveByNamedQuery("JOB_COUNT");
		if (!countList.isEmpty()) {
			return ((Long) countList.get(0)).longValue();
		}
		return 0;
	}

	@Override
	public void cancelImportJob(final String importJobProcessId, final CmUser cancelledBy) {
		List<ImportNotification> launchNotifications = 
			importNotificationDao.findByProcessId(importJobProcessId, ImportAction.LAUNCH_IMPORT);
		if (launchNotifications.isEmpty() || launchNotifications.size() > 1) {
			LOG.warn("Exactly one import job launch notification is expected but found: " + launchNotifications.size());
			return;
		}
		ImportNotification launchNotification = launchNotifications.iterator().next();
		ImportNotification notification = getBean(ContextIdNames.IMPORT_NOTIFICATION);
		notification.setImportJob(launchNotification.getImportJob());
		notification.setAction(ImportAction.CANCEL_IMPORT);
		notification.setInitiator(cancelledBy);
		notification.setProcessId(importJobProcessId);
		
		// set the date created using the time provided by the TimeService
		notification.setDateCreated(timeService.getCurrentTime());
		
		importNotificationDao.add(notification);
	}
	
	/**
	 * @return the assetRepository
	 */
	protected AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
	
	/**
	 * @param catalogService instance of CatalogService to use
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	
	/**
	 * @param warehouseService instance of WarehouseService to use
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	/**
	 * @param storeService instance of StoreService to use
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 *
	 * @return the importNotificationDao
	 */
	protected ImportNotificationDao getImportNotificationDao() {
		return importNotificationDao;
	}

	/**
	 *
	 * @param importNotificationDao the importNotificationDao to set
	 */
	public void setImportNotificationDao(final ImportNotificationDao importNotificationDao) {
		this.importNotificationDao = importNotificationDao;
	}

	/**
	 *
	 * @return the importJobStatusHandler
	 */
	protected ImportJobStatusHandler getImportJobStatusHandler() {
		return importJobStatusHandler;
	}

	/**
	 *
	 * @param importJobStatusHandler the importJobStatusHandler to set
	 */
	public void setImportJobStatusHandler(final ImportJobStatusHandler importJobStatusHandler) {
		this.importJobStatusHandler = importJobStatusHandler;
	}

	/**
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService() {
		return timeService;
	}
	
	@Override
	public List<ImportJob> findByGuids(final Set<String> importJobGuids) {
		sanityCheck();

		if (importJobGuids == null || importJobGuids.isEmpty()) {
			return Collections.emptyList();
		}

		final List<ImportJob> result = getPersistenceEngine().retrieveByNamedQueryWithList(
				"IMPORT_JOB_FIND_BY_GUIDS",
				PLACEHOLDER_FOR_LIST,
				importJobGuids);
		return result;
	}
}
