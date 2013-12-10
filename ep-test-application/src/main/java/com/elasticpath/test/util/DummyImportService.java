package com.elasticpath.test.util;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.ElasticPath;
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
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.dataimport.ImportJobExistException;
import com.elasticpath.service.dataimport.ImportService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Implementation of Import Service which does nothing, for substitution in tests.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class DummyImportService implements ImportService {

	private static final String NOT_USED = "Dummy import service should not be used";

	@Override
	public void cancelImportJob(final String importJobProcessId, final CmUser user) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public long countCatalogJobs(final Long... catalogUids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public long countCustomerJobs(final Long... storeUids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public long countPriceListImportJobs(final String... priceListGuids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public long countWarehouseJobs(final Long... warehouseUids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public Catalog findCatalog(final String name) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportDataType findImportDataType(final String name) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportJob findImportJob(final String name) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public Store findStore(final String name) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public Warehouse findWarehouse(final String name) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportDataType> getCatalogImportDataTypes(final long catalogUid) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportDataType> getCustomerImportDataTypes() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportJob getImportJob(final long importJobUid) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportJobStatus getImportJobStatus(final String importJobProcessId) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportType getImportType(final int importTypeId) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<List<String>> getPreviewData(final ImportJob importJob, final int maxPreviewRows) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<List<String>> getPreviewData(final ImportJob importJob, final int maxPreviewRows, final boolean returnRawData) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportDataType> getPriceListImportDataTypes() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<String> getTitleLine(final ImportJobRequest importJobRequest) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportDataType> getWarehouseImportDataTypes() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportDataType initImportDataTypeLocalesAndCurrencies(final ImportDataType importDataType, final ImportJob importJob) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportJob> listCatalogImportJobs(final int startIndex, final int maxResults, final Long... catalogUids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<Catalog> listCatalogs() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportJob> listCustomerImportJobs(final int startIndex, final int maxResults, final Long... storeUids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportDataType> listImportDataTypes() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportJob> listImportJobs() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportJob> listImportJobs(final int startIndex, final int maxResults) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportType> listImportTypes() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportJob> listPriceListImportJobs(final String... priceListGuids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<Store> listStores() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportJob> listWarehouseImportJobs(final int startIndex, final int maxResults, final Long... warehouseUids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<Warehouse> listWarehouses() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public void remove(final ImportJob importJob) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportJob saveOrUpdateImportJob(final ImportJob importJob) throws ImportJobExistException {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ImportJobStatus scheduleImport(final ImportJobRequest importJobRequest) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportBadRow> validateCsvFormat(final ImportJobRequest importJobRequest) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public List<ImportBadRow> validateMappings(final ImportJobRequest importJobRequest) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public PersistenceEngine getPersistenceEngine() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public ElasticPath getElasticPath() {
		throw new UnsupportedOperationException(NOT_USED);
	}

	@Override
	public void setElasticPath(final ElasticPath elasticpath) {
		throw new UnsupportedOperationException(NOT_USED);
	}
	
	@Override
	public List<ImportJob> findByGuids(Set<String> importJobGuids) {
		throw new UnsupportedOperationException(NOT_USED);
	}

}
