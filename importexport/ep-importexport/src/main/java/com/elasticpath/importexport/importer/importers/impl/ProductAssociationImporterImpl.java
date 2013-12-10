package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * ProductAssociation importer implementation.
 */
public class ProductAssociationImporterImpl extends AbstractImporterImpl<ProductAssociation, ProductAssociationDTO> {

	private FetchGroupLoadTuner loadTuner;
	private static final Logger LOG = Logger.getLogger(ProductAssociationImporterImpl.class);
	private DomainAdapter<ProductAssociation, ProductAssociationDTO> productAssociationAdapter;
	private ProductAssociationService productAssociationService;
	
	@Override
	protected ProductAssociation findPersistentObject(final ProductAssociationDTO dto) {		
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode(dto.getSourceProductCode());
		criteria.setTargetProductCode(dto.getTargetProductCode());
		criteria.setCatalogCode(dto.getCatalogCode());
		criteria.setAssociationType(dto.getProductAssociationType().typeId());
		criteria.setWithinCatalogOnly(true);

		List<ProductAssociation> productAssociations = productAssociationService.findByCriteria(criteria, loadTuner);
 		if (!productAssociations.isEmpty()) {
			if (productAssociations.size() != 1) {
				LOG.info(new Message("IE-30800"));
			}
			return productAssociations.get(0);
		}

		return null;
	}
	
	@Override
	public boolean executeImport(final ProductAssociationDTO object) {
		sanityCheck();
		if (getContext().getImportConfiguration().getImporterConfiguration(
				JobType.PRODUCT).getImportStrategyType().equals(ImportStrategyType.INSERT)
				&& !getContext().isProductChanged(object.getSourceProductCode())) {
			return false;
		}
		
		return super.executeImport(object);
	}
	
	@Override
	protected String getDtoGuid(final ProductAssociationDTO dto) {
		return null;
	}
	
	@Override
	protected void setImportStatus(final ProductAssociationDTO object) {
		getStatusHolder().setImportStatus("(" + object.getSourceProductCode() + "-" + object.getTargetProductCode() + ")");
	}

	@Override
	protected DomainAdapter<ProductAssociation, ProductAssociationDTO> getDomainAdapter() {
		return productAssociationAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return ProductAssociationDTO.ROOT_ELEMENT;
	}

	/**
	 * Gets the implementation of collections strategy for product associations.
	 * 
	 * @return the appropriate collections strategy
	 */
	@Override
	protected CollectionsStrategy<ProductAssociation, ProductAssociationDTO> getCollectionsStrategy() {
		ProductAssociationsCollectionsStrategy collectionsStrategy = new ProductAssociationsCollectionsStrategy(getContext()
				.getImportConfiguration().getImporterConfiguration(JobType.PRODUCT));
		collectionsStrategy.setProductAssociationService(productAssociationService);
		collectionsStrategy.setProductAssociationLoadTuner(loadTuner);
		return collectionsStrategy;
	}

	/**
	 * Gets ProductAssociationAdapter.
	 * 
	 * @return the productAssociationAdapter
	 */
	public DomainAdapter<ProductAssociation, ProductAssociationDTO> getProductAssociationAdapter() {
		return productAssociationAdapter;
	}

	/**
	 * Sets ProductAssociationAdapter.
	 * 
	 * @param productAssociationAdapter the productAssociationAdapter to set
	 */
	public void setProductAssociationAdapter(final DomainAdapter<ProductAssociation, ProductAssociationDTO> productAssociationAdapter) {
		this.productAssociationAdapter = productAssociationAdapter;
	}

	/**
	 * Gets ProductAssociationService.
	 * 
	 * @return the productAssociationService
	 */
	public ProductAssociationService getProductAssociationService() {
		return productAssociationService;
	}

	/**
	 * Sets ProductAssociationService.
	 * 
	 * @param productAssociationService the productAssociationService to set
	 */
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}

	/**
	 * Sets the load tuner.
	 * 
	 * @param loadTuner the load tuner to use
	 */
	public void setProductAssociationLoadTuner(final FetchGroupLoadTuner loadTuner) {
		this.loadTuner = loadTuner;
	}

	@Override
	public Class<? extends ProductAssociationDTO> getDtoClass() {
		return ProductAssociationDTO.class;
	}

	/**
	 * Implementation of <code>CollectionsStrategy</code> for product associations collection.
	 */
	private static final class ProductAssociationsCollectionsStrategy implements CollectionsStrategy<ProductAssociation, ProductAssociationDTO> {

		private ProductAssociationService productAssociationService;

		private final boolean isProductAssociationsClearCollection;

		private final Set<String> processedProductCodesSet = new HashSet<String>();

		private FetchGroupLoadTuner loadTuner;

		/**
		 * Constructs the product associations collections strategy.
		 * 
		 * @param importerConfiguration the importer configuration
		 */
		public ProductAssociationsCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			isProductAssociationsClearCollection = importerConfiguration.getCollectionStrategyType(DependentElementType.PRODUCT_ASSOCIATIONS).equals(
					CollectionStrategyType.CLEAR_COLLECTION);

		}

		@Override
		public void prepareCollections(final ProductAssociation domainObject, final ProductAssociationDTO dto) {
			if (isProductAssociationsClearCollection) {
				useClearCollection(domainObject, dto);
			}

		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return false;
		}

		private void useClearCollection(final ProductAssociation association, final ProductAssociationDTO dto) {
			if (processedProductCodesSet.contains(dto.getSourceProductCode())) {
				return;
			}

			ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
			criteria.setSourceProductCode(dto.getSourceProductCode());
			List<ProductAssociation> productAssociations = productAssociationService.findByCriteria(criteria, loadTuner);

			Set<Long> associationsToRemove = new HashSet<Long>();

			processedProductCodesSet.add(dto.getSourceProductCode());
			for (ProductAssociation productAssociation : productAssociations) {
				if (!association.isPersisted() || !productAssociation.equals(association)) {
					productAssociationService.remove(productAssociation);
					associationsToRemove.add(productAssociation.getUidPk());
				}
			}
		}

		/**
		 * Sets the productAssociationService.
		 * 
		 * @param productAssociationService the productAssociationService to set
		 */
		public void setProductAssociationService(final ProductAssociationService productAssociationService) {
			this.productAssociationService = productAssociationService;
		}

		public void setProductAssociationLoadTuner(final FetchGroupLoadTuner loadTuner) {
			this.loadTuner = loadTuner;
		}
	}
}
