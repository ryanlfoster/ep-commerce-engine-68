package com.elasticpath.service.dataimport;

import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide services of the import manager.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ImportService extends EpPersistenceService {

	/**
	 * List all saved import jobs.
	 * 
	 * @return a list of saved import jobs.
	 */
	List<ImportJob> listImportJobs();

	/**
	 * List all saved import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @return a list of saved import jobs.
	 */
	List<ImportJob> listImportJobs(final int startIndex, final int maxResults);

	/**
	 * List all import data types.
	 * 
	 * @return a list of import data types.
	 */
	List<ImportDataType> listImportDataTypes();

	/**
	 * List all catalog import data types for the catalog with the specified UID.
	 * 
	 * @param catalogUid the catalog UID
	 * @return a list of catalog import data types
	 */
	List<ImportDataType> getCatalogImportDataTypes(long catalogUid);

	/**
	 * List all customer import data types.
	 * 
	 * @return a list of customer import data types
	 */
	List<ImportDataType> getCustomerImportDataTypes();

	/**
	 * List all price list import data types.
	 * 
	 * @return a list of price list import data types
	 */
	List<ImportDataType> getPriceListImportDataTypes();
	
	/**
	 * List all warehouse import data types.
	 * 
	 * @return a list of warehouse import data types
	 */
	List<ImportDataType> getWarehouseImportDataTypes();

	/**
	 * List all catalogs.
	 * 
	 * @return a list of catalogs.
	 * @deprecated Use the catalog service where possible
	 */
	@Deprecated
	List<Catalog> listCatalogs();

	/**
	 * Find a catalog by name.
	 * 
	 * @param name the catalog name
	 * @return the catalog with the given name if it exist, otherwise, <code>null</code>.
	 * @deprecated Use the CatalogService directly where possible
	 */
	@Deprecated
	Catalog findCatalog(String name);

	/**
	 * List all stores.
	 * 
	 * @return a list of stores.
	 * @deprecated use the StoreService directly for this operation where possible
	 */
	@Deprecated
	List<Store> listStores();

	/**
	 * Find a store by name.
	 * 
	 * @param name the store name
	 * @return the store with the given name if it exist, otherwise, <code>null</code>.
	 */
	Store findStore(String name);

	/**
	 * List all warehouses.
	 * 
	 * @return a list of warehouses.
	 * @deprecated use the WarehouseSerice directly for this operation where possible
	 */
	@Deprecated
	List<Warehouse> listWarehouses();

	/**
	 * Find a warehouse by name.
	 * 
	 * @param name the warehouse name
	 * @return the warehouse with the given name if it exist, otherwise, <code>null</code>.
	 */
	Warehouse findWarehouse(String name);

	/**
	 * List all import types.
	 * 
	 * @return a list of import types.
	 */
	List<ImportType> listImportTypes();

	/**
	 * Returns the import type with the given id.
	 * 
	 * @param importTypeId the import type id
	 * @return the import type with the given id
	 */
	ImportType getImportType(int importTypeId);

	/**
	 * Find an import job by name.
	 * 
	 * @param name the import job name
	 * @return the import job of the given name if it exist, otherwise, <code>null</code>.
	 */
	ImportJob findImportJob(String name);

	/**
	 * Find an import data type by name.
	 * 
	 * @param name the import data type name
	 * @return the import data type of the given name if it exist, otherwise, <code>null</code>.
	 */
	ImportDataType findImportDataType(String name);

	/**
	 * Initialize locales for importDataType.
	 *
	 * @param importDataType import data type
	 * @param importJob for retrieving catalog/store and getting its locales
	 * @return ImportDataType with changed locales
	 */
	ImportDataType initImportDataTypeLocalesAndCurrencies(final ImportDataType importDataType, final ImportJob importJob);


	/**
	 * Get the import job with the given UID. Return null if no matching record exists.
	 * 
	 * @param importJobUid the importJob UID, give 0 if you want to create a new import job.
	 * @return the importJob if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ImportJob getImportJob(long importJobUid) throws EpServiceException;

	/**
	 * Validate the csv format of the import data file.
	 * 
	 * @param importJobRequest the import job request
	 * @return a list of lines that not complying with the csv format. If all lines are good, then returns an empty list.
	 */
	List<ImportBadRow> validateCsvFormat(ImportJobRequest importJobRequest);

	/**
	 * Returns the preview data of the import data file.
	 * 
	 * @param importJob the import job
	 * @param maxPreviewRows maximum quantity of rows for preview data, that will be imported.
	 *  
	 * @return a list of records for previewing
	 */
	List<List<String>> getPreviewData(ImportJob importJob, int maxPreviewRows);
	
	/**
	 * Returns the preview data of the import data file.
	 * 
	 * @param importJob the import job
	 * @param maxPreviewRows maximum rows for preview data, that will be imported.
	 * @param returnRawData the boolean value to determine if the result need to be filtered. True will return the raw data.
	 *  
	 * @return a list of records for previewing
	 */
	List<List<String>> getPreviewData(ImportJob importJob, int maxPreviewRows, boolean returnRawData);

	/**
	 * Validate the import mappings specified in the import job.
	 * 
	 * @param importJobRequest the import job request
	 * @return a list of lines that not complying with the mappings. If all lines are good, then returns an empty list.
	 */
	List<ImportBadRow> validateMappings(ImportJobRequest importJobRequest);

	/**
	 * Save or update the given import job.
	 * 
	 * @param importJob the importJob to save or update
	 * @return the updated instance of the importJob
	 * @throws ImportJobExistException - if the specified import job name is already in use.
	 * @see ImportJob
	 */
	ImportJob saveOrUpdateImportJob(final ImportJob importJob) throws ImportJobExistException;

	/**
	 * Schedules an import for execution.
	 * 
	 * @param importJobRequest the import job request
	 * @return the import job status right after the scheduling
	 */
	ImportJobStatus scheduleImport(ImportJobRequest importJobRequest);
	
	/**
	 * Returns the title line of the import csv data file.
	 * 
	 * @param importJobRequest the import job request
	 * @return a list of fields of the title line
	 */
	List<String> getTitleLine(ImportJobRequest importJobRequest);

	/**
	 * Retrieves the current status of an import job.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @return the import job status
	 */
	ImportJobStatus getImportJobStatus(String importJobProcessId);
	
	/**
	 * Delete the importJob.
	 * 
	 * @param importJob the job to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final ImportJob importJob);

	/**
	 * Gets the count of jobs stored in the database.
	 * 
	 * @return the number of jobs stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	long count();

	/**
	 * List all saved catalog import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @param catalogUids limit the import jobs to the ones for the catalogs listed.
	 *                     Listing none will return all catalog import jobs.
	 * @return a list of saved import jobs.
	 */
	List<ImportJob> listCatalogImportJobs(final int startIndex, final int maxResults, final Long ... catalogUids);
	
	/**
     * @param priceListGuids optional filter price list jobs by available to user price lists 
	 * @return the number of Price List import jobs stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	long countPriceListImportJobs(final String ... priceListGuids);

	/**
	 * List all saved import jobs that are dependent on PriceListDescriptors.
     * @param priceListGuids optional filter price list jobs by available to user price lists 
	 * @return the list of saved jobs
	 */
	List<ImportJob> listPriceListImportJobs(final String ... priceListGuids);

	/**
	 * Gets the count of jobs stored in the database.
	 * 
	 * @param catalogUids (optional) restrict the count the import jobs in the specified catalogs.
	 * @return the number of jobs stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	long countCatalogJobs(final Long ... catalogUids);

	/**
	 * List all saved customer import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @param storeUids limit the import jobs to the ones for the stores listed.
	 * 					 Listing none will return all customer import jobs.
	 * @return a list of saved import jobs.
	 */
	List<ImportJob> listCustomerImportJobs(final int startIndex, final int maxResults, final Long ... storeUids);

	/**
	 * Gets the count of jobs stored in the database.
	 * 
	 * @param storeUids (optional) restrict the count the import jobs in the specified stores.
	 * @return the number of jobs stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	long countCustomerJobs(final Long ... storeUids);

	/**
	 * List all warehouse import jobs.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @param warehouseUids limit the iport jobs to the ones for the warehouses listed.
	 * 						 Listing none will return all warehouse import jobs.
	 * @return a list of saved import jobs.
	 */
	List<ImportJob> listWarehouseImportJobs(final int startIndex, final int maxResults, final Long ... warehouseUids);

	/**
	 * Gets the count of warehouse jobs stored in the database.
	 * 
	 * @param warehouseUids (optional) restrict the count the import jobs in the specified warehouses.
	 * @return the number of ImportJob stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	long countWarehouseJobs(final Long ... warehouseUids);

	/**
	 * Cancel an import job process by its ID.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param user the CM user
	 */
	void cancelImportJob(String importJobProcessId, CmUser user);
	
	/**
	 * Returns a list of <code>ImportJob</code> based on the given guids.
	 *
	 * @param importJobGuids a set of import job guids
	 * @return a list of <code>ImportJob</code>s
	 */
	List<ImportJob> findByGuids(final Set<String> importJobGuids);
	
}
