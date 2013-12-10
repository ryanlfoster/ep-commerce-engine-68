package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.dao.ProductDao;

/**
 * Product importer implementation.
 */
class ProductImporterImpl extends AbstractImporterImpl<Product, ProductDTO> {

	private DomainAdapter<Product, ProductDTO> productAdapter;

	private ProductDao productDao;
	
	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Product, ProductDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		
		getSavingStrategy().setLifecycleListener(new DefaultLifecycleListener() {
			/**
			 * Memorize updated or saved products.
			 */
			@Override
			public void afterSave(final Persistable persistable) {
				context.addChangedProduct(((Product) persistable).getCode());
			}
		});
	}
	
	@Override
	protected String getDtoGuid(final ProductDTO dto) {
		return dto.getCode();
	}


	@Override
	protected Product findPersistentObject(final ProductDTO dto) {
		return productDao.findByGuid(dto.getCode(), null);
	}
	
	@Override
	protected void setImportStatus(final ProductDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCode() + ")");
	}

	@Override
	protected DomainAdapter<Product, ProductDTO> getDomainAdapter() {
		return productAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return ProductDTO.ROOT_ELEMENT;
	}

	/**
	 * Returns the collections strategy for product.
	 * 
	 * @return appropriate collections strategy
	 */
	@Override
	protected CollectionsStrategy<Product, ProductDTO> getCollectionsStrategy() {
		return new ProductCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.PRODUCT));
	}

	/**
	 * Gets the productAdapter.
	 * 
	 * @return the productAdapter
	 */
	public DomainAdapter<Product, ProductDTO> getProductAdapter() {
		return productAdapter;
	}

	/**
	 * Sets the productAdapter.
	 * 
	 * @param productAdapter the productAdapter to set
	 */
	public void setProductAdapter(final DomainAdapter<Product, ProductDTO> productAdapter) {
		this.productAdapter = productAdapter;
	}
	
	/**
	 * Gets the product dao.
	 * 
	 * @return product dao
	 */
	public ProductDao getProductDao() {
		return productDao;
	}

	/**
	 * Sets the product dao.
	 * 
	 * @param productDao the product dao
	 */
	public void setProductDao(final ProductDao productDao) {
		this.productDao = productDao;
	}

	
	@Override
	public Class<? extends ProductDTO> getDtoClass() {
		return ProductDTO.class;
	}

	/**
	 * Implementation of <code>CollectionsStrategy</code> interface for product object.
	 */
	private static final class ProductCollectionsStrategy implements CollectionsStrategy<Product, ProductDTO> {

		private final boolean isSkuAttributesClearCollection;

		private final boolean isProductAttributesClearCollection;

		private final boolean isProductSkusClearCollection;

		/**
		 * Constructs product collections strategy.
		 * 
		 * @param importerConfiguration the importer configuration
		 */
		public ProductCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			CollectionStrategyType skuAttributesCollectionStrategyType = importerConfiguration
					.getCollectionStrategyType(DependentElementType.SKU_ATTRIBUTES);

			isSkuAttributesClearCollection = skuAttributesCollectionStrategyType.equals(CollectionStrategyType.CLEAR_COLLECTION);

			CollectionStrategyType productAttributesCollectionStrategyType = importerConfiguration
					.getCollectionStrategyType(DependentElementType.PRODUCT_ATTRIBUTES);

			isProductAttributesClearCollection = productAttributesCollectionStrategyType.equals(CollectionStrategyType.CLEAR_COLLECTION);

			CollectionStrategyType productSkuCollectionStrategyType = importerConfiguration
					.getCollectionStrategyType(DependentElementType.PRODUCT_SKUS);

			isProductSkusClearCollection = productSkuCollectionStrategyType.equals(CollectionStrategyType.CLEAR_COLLECTION);

		}

		@Override
		public void prepareCollections(final Product product, final ProductDTO productDTO) {
			prepareProductSkuCollection(product, productDTO);
			prepareAttributesCollection(product);
			prepareSkuAttributesCollection(product);

		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		private void prepareSkuAttributesCollection(final Product product) {
			if (isSkuAttributesClearCollection) {
				for (ProductSku productSku : product.getProductSkus().values()) {
					productSku.getAttributeValueMap().clear();
				}

			}
		}

		private void prepareAttributesCollection(final Product product) {
			if (isProductAttributesClearCollection) {
				product.getAttributeValueMap().clear();
			}
		}
		private void prepareProductSkuCollection(final Product product, final ProductDTO productDTO) {

			if (isProductSkusClearCollection || !product.getProductType().isWithMultipleSkus()) {
				Map<String, ProductSku> productSkuMap = new HashMap<String, ProductSku>();
				for (ProductSkuDTO productSkuDTO : productDTO.getProductSkus()) {					
					ProductSku productSku = product.getSkuByGuid(productSkuDTO.getGuid());					
					if (productSku != null) {
						productSkuMap.put(productSkuDTO.getSkuCode(), productSku);
					}
				}
				product.setProductSkus(productSkuMap);
			}
		}
	}
}
