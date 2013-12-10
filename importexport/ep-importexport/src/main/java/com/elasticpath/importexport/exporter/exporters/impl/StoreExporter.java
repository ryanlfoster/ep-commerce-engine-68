package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.store.StoreAssociationDTO;
import com.elasticpath.common.dto.store.StoreDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.store.StoreAssociationService;
import com.elasticpath.service.store.StoreService;

/**
 * Implements the methods required to export {@link Store} objects.
 */
public class StoreExporter extends AbstractExporterImpl<Store, StoreDTO, String> {

	private static final Logger LOG = Logger.getLogger(StoreExporter.class);

	private StoreService storeService;

	private StoreAssociationService storeAssociationService;

	private ImportExportSearcher importExportSearcher;
	
	private List<String> storeCodes;

	private DomainAdapter<Store, StoreDTO> storeAdapter;

	private DomainAdapter<Store, StoreAssociationDTO> storeAssociationAdapter;

	public JobType getJobType() {
		return JobType.STORE;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Store.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		storeCodes = new ArrayList<String>();
		storeCodes.addAll(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), 
				EPQueryType.STORE));
		
		LOG.info("The list for " + storeCodes.size() + " stores retrieved from the database.");
	}

	@Override
	protected Class<? extends StoreDTO> getDtoClass() {
		return StoreDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {

		Set<String> workingSet = new HashSet<String>(this.storeCodes);

		if (getContext().getDependencyRegistry().supportsDependency(Store.class)) {
			workingSet.addAll(getContext().getDependencyRegistry().getDependentGuids(Store.class));
		}

		/* Fetch all stores that are associated to the stores we're already exporting (recursively) */
		Set<String> associatedStoreCodes = storeAssociationService.getAllAssociatedStoreCodes(workingSet);
		workingSet.addAll(associatedStoreCodes);

		this.storeCodes = new ArrayList<String>(workingSet);

		return storeCodes;
	}

	@Override
	protected List<Store> findByIDs(final List<String> subList) {
		ArrayList<Store> stores = new ArrayList<Store>();
		for (String storeCode : subList) {
			Store lite = storeService.findStoreWithCode(storeCode);

			FetchGroupLoadTunerImpl loadTunerAll = new FetchGroupLoadTunerImpl();
			loadTunerAll.addFetchGroup(FetchGroupConstants.ALL);
			stores.add(storeService.getTunedStore(lite.getUidPk(), loadTunerAll));
		}
		return stores;
	}

	@Override
	protected void addDependencies(final List<Store> stores, final DependencyRegistry dependencyRegistry) {

		for (Store store : stores) {

			if (dependencyRegistry.supportsDependency(StoreAssociation.class)) {
				dependencyRegistry.addGuidDependency(StoreAssociation.class, store.getCode());
			}

			if (dependencyRegistry.supportsDependency(Catalog.class)) {
				dependencyRegistry.addUidDependency(Catalog.class, store.getCatalog().getUidPk());
			}

			if (dependencyRegistry.supportsDependency(Warehouse.class)) {
				for (Warehouse warehouse : store.getWarehouses()) {
					dependencyRegistry.addUidDependency(Warehouse.class, warehouse.getUidPk());
				}
			}

			if (dependencyRegistry.supportsDependency(TaxJurisdiction.class)) {
				for (TaxJurisdiction jurisdiction : store.getTaxJurisdictions()) {
					dependencyRegistry.addGuidDependency(TaxJurisdiction.class, jurisdiction.getGuid());
				}
			}

			if (dependencyRegistry.supportsDependency(PaymentGateway.class)) {
				for (PaymentGateway gateway : store.getPaymentGateways()) {
					dependencyRegistry.addGuidDependency(PaymentGateway.class, gateway.getName());
				}
			}
		}
	}

	@Override
	protected DomainAdapter<Store, StoreDTO> getDomainAdapter() {
		return storeAdapter;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @param storeAssociationService the storeAssociationService to set
	 */
	public void setStoreAssociationService(final StoreAssociationService storeAssociationService) {
		this.storeAssociationService = storeAssociationService;
	}

	public void setStoreAdapter(final DomainAdapter<Store, StoreDTO> storeAdapter) {
		this.storeAdapter = storeAdapter;
	}

	/**
	 * @return the storeAssociationAdapter
	 */
	public DomainAdapter<Store, StoreAssociationDTO> getStoreAssociationAdapter() {
		return storeAssociationAdapter;
	}

	/**
	 * @param storeAssociationAdapter the storeAssociationAdapter to set
	 */
	public void setStoreAssociationAdapter(final DomainAdapter<Store, StoreAssociationDTO> storeAssociationAdapter) {
		this.storeAssociationAdapter = storeAssociationAdapter;
	}

	/**
	 * @return importExportSearcher
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * Sets importexport searcher.
	 * 
	 * @param importExportSearcher importExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	
	
}
