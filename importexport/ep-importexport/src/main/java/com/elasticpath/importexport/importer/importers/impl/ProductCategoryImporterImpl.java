package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.productcategories.ProductCategoryAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.productcategory.CatalogCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoryDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.catalog.ProductService;

/**
 * Product category importer implementation.
 */
public class ProductCategoryImporterImpl extends AbstractImporterImpl<Product, ProductCategoriesDTO> {

	private ProductCategoryAdapter productCategoryAdapter;

	private ProductService productService;

	private ProductLoadTuner productLoadTuner;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Product, ProductCategoriesDTO> savingStrategy) {
		super.initialize(
				context,
				AbstractSavingStrategy.<Product, ProductCategoriesDTO> createStrategy(ImportStrategyType.UPDATE,
						savingStrategy.getSavingManager()));
	}

	@Override
	protected Product findPersistentObject(final ProductCategoriesDTO dto) {
		if (getContext().getImportConfiguration().getImporterConfiguration(
				JobType.PRODUCT).getImportStrategyType().equals(ImportStrategyType.INSERT)
				&& !getContext().isProductChanged(dto.getProductCode())) {
			return null;
		}
		return productService.findByGuid(dto.getProductCode(), productLoadTuner);
	}
	
	@Override
	protected String getDtoGuid(final ProductCategoriesDTO dto) {
		return dto.getProductCode();
	}
	
	@Override
	protected void setImportStatus(final ProductCategoriesDTO object) {
		getStatusHolder().setImportStatus("(for product " + object.getProductCode() + ")");
	}

	@Override
	protected DomainAdapter<Product, ProductCategoriesDTO> getDomainAdapter() {
		return productCategoryAdapter;
	}

	@Override
	protected CollectionsStrategy<Product, ProductCategoriesDTO> getCollectionsStrategy() {
		// / FIXME: retrieve nested ProductCategoryCollectionsStrategy class and inject caching service by spring.
		return new ProductCategoryCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.PRODUCT),
				productCategoryAdapter.getCachingService());
	}

	@Override
	public String getImportedObjectName() {
		return ProductCategoriesDTO.ROOT_ELEMENT;
	}

	@Override
	public int getObjectsQty(final ProductCategoriesDTO dto) {
		int result = 0;
		for (CatalogCategoriesDTO catalogCategoriesDTO : dto.getCatalogCategoriesDTOList()) {
			result += catalogCategoriesDTO.getProductCategoryDTOList().size();
		}
		return result;
	}

	/**
	 * Gets the productCategoryAdapter.
	 * 
	 * @return the productCategoryAdapter
	 */
	public ProductCategoryAdapter getProductCategoryAdapter() {
		return productCategoryAdapter;
	}

	/**
	 * Sets the productCategoryAdapter.
	 * 
	 * @param productCategoryAdapter the productCategoryAdapter to set
	 */
	public void setProductCategoryAdapter(final ProductCategoryAdapter productCategoryAdapter) {
		this.productCategoryAdapter = productCategoryAdapter;
	}

	/**
	 * Gets the productService.
	 * 
	 * @return the productService
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 * Sets the productService.
	 * 
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Gets the productLoadTuner.
	 * 
	 * @return the productLoadTuner
	 */
	public ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	/**
	 * Sets the productLoadTuner.
	 * 
	 * @param productLoadTuner the productLoadTuner to set
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	@Override
	public Class<? extends ProductCategoriesDTO> getDtoClass() {
		return ProductCategoriesDTO.class;
	}

	/**
	 * Implementation of <code>CollectionsStrategy</code> interface for product categories.
	 */
	private static final class ProductCategoryCollectionsStrategy implements CollectionsStrategy<Product, ProductCategoriesDTO> {

		private final boolean isProductCategoryClearCollection;

		private final CachingService cachingService;

		/**
		 * Constructs the product category collections strategy.
		 * 
		 * @param importerConfiguration the importer configuration
		 * @param cachingService caching service containing cached domain objects
		 */
		public ProductCategoryCollectionsStrategy(final ImporterConfiguration importerConfiguration, final CachingService cachingService) {
			CollectionStrategyType collectionStrategyType = importerConfiguration
					.getCollectionStrategyType(DependentElementType.PRODUCT_CATEGORY_ASSIGNMENTS);
			isProductCategoryClearCollection = collectionStrategyType.equals(CollectionStrategyType.CLEAR_COLLECTION);
			this.cachingService = cachingService;
		}

		@Override
		public void prepareCollections(final Product domainObject, final ProductCategoriesDTO dto) {
			if (isProductCategoryClearCollection) {
				productCategoryClearCollection(domainObject, dto);
			}
		}

		private void productCategoryClearCollection(final Product domainObject, final ProductCategoriesDTO dto) {
			final Set<Category> newCategorySet = new HashSet<Category>();
			final Catalog masterCatalog = domainObject.getMasterCatalog();
			if (masterCatalog != null) {
				newCategorySet.add(domainObject.getDefaultCategory(masterCatalog));
			}
			
			for (CatalogCategoriesDTO catalogCategoriesDTO : dto.getCatalogCategoriesDTOList()) {
				for (ProductCategoryDTO productCategoryDTO : catalogCategoriesDTO.getProductCategoryDTOList()) {
					Category category = cachingService.findCategoryByCode(productCategoryDTO.getCategoryCode(), catalogCategoriesDTO
							.getCatalogCode());

					if (category != null) {
						newCategorySet.add(category);
						if (productCategoryDTO.isDefaultCategory()) {
							domainObject.setCategoryAsDefault(category);
						}
					}
				}
			}

			domainObject.setCategories(newCategorySet);
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}
}
