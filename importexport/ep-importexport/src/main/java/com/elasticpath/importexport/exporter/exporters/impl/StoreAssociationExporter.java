package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.common.dto.store.StoreAssociationDTO;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.store.StoreService;

/**
 * Exporter for {@link com.elasticpath.importexport.exporter.exporters.impl.StoreAssociation}s. 
 */
public class StoreAssociationExporter extends AbstractExporterImpl<Store, StoreAssociationDTO, String> {
	
//	private static final Logger LOG = Logger.getLogger(StoreAssociationExporter.class);

	private StoreService storeService;
	
	private DomainAdapter<Store, StoreAssociationDTO> storeAssociationAdapter;
	
	private List<String> storeCodes;
	
	public JobType getJobType() {
		return JobType.STORE_ASSOCIATION;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { StoreAssociation.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		storeCodes = new ArrayList<String>();
		// does nothing as is dependent on Store.
	}
	
	@Override
	protected Class<? extends StoreAssociationDTO> getDtoClass() {
		return StoreAssociationDTO.class;
	}
	
	@Override
	protected List<String> getListExportableIDs() {

		if (getContext().getDependencyRegistry().supportsDependency(StoreAssociation.class)) {
			storeCodes.addAll(getContext().getDependencyRegistry().getDependentGuids(StoreAssociation.class));
		}

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
	protected DomainAdapter<Store, StoreAssociationDTO> getDomainAdapter() {
		return storeAssociationAdapter;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
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

}
