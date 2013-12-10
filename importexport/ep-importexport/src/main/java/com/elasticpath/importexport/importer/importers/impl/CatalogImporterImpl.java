package com.elasticpath.importexport.importer.importers.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CatalogObject;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.AttributeAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.BrandAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.CatalogAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.CategoryTypeAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.ProductTypeAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.SkuOptionAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.SynonymGroupAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CategoryTypeDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionDTO;
import com.elasticpath.importexport.common.dto.catalogs.SynonymGroupDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Catalog importer implementation. TODO: This class must be total refactored. (Be More Creative)
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public class CatalogImporterImpl extends AbstractImporterImpl<Catalog, CatalogDTO> {
	private CatalogAdapter catalogAdapter;

	private BrandAdapter brandAdapter;

	private AttributeAdapter attributeAdapter;

	private CategoryTypeAdapter categoryTypeAdapter;

	private CatalogService catalogService;

	private BrandService brandService;

	private CachingService cachingService;

	private CategoryTypeService categoryTypeService;

	private AttributeService attributeService;

	private ProductTypeService productTypeService;

	private ProductTypeAdapter productTypeAdapter;

	private SkuOptionAdapter skuOptionAdapter;

	private SkuOptionService skuOptionService;

	private SynonymGroupAdapter synonymGroupAdapter;

	private SynonymGroupService synonymGroupService;

	private static final Logger LOG = Logger.getLogger(CatalogImporterImpl.class);

	private SavingStrategy<Attribute, AttributeDTO> attributeSavingStrategy;

	private SavingStrategy<SkuOption, SkuOptionDTO> skuOptionSavingStrategy;
	private SavingManager<? extends Persistable> commonSavingManager;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Catalog, CatalogDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		commonSavingManager = savingStrategy.getSavingManager();

		getSavingStrategy().setSavingManager(new SavingManager<Catalog>() {

			@Override
			public Catalog update(final Catalog persistable) {
				return catalogService.saveOrUpdate(persistable);
			}

			@Override
			public void save(final Catalog persistable) {
				update(persistable);
			}

		});

		final SavingManager<Attribute> attributeSavingManager = new SavingManager<Attribute>() {

			@Override
			public Attribute update(final Attribute persistable) {
				return attributeService.update(persistable);
			}

			@Override
			public void save(final Attribute persistable) {
				attributeService.add(persistable);
			}

		};

		final SavingManager<SkuOption> skuOptionSavingManager = new SavingManager<SkuOption>() {

			@Override
			public SkuOption update(final SkuOption persistable) {
				return skuOptionService.update(persistable);
			}

			@Override
			public void save(final SkuOption persistable) {
				skuOptionService.add(persistable);
			}
		};

		attributeSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE, attributeSavingManager);
		attributeSavingStrategy.setDomainAdapter(attributeAdapter);

		skuOptionSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE, skuOptionSavingManager);
		skuOptionSavingStrategy.setDomainAdapter(skuOptionAdapter);
		
	}

	@Override
	protected Catalog findPersistentObject(final CatalogDTO dto) {
		Catalog catalog = cachingService.findCatalogByCode(dto.getCode());

		// FIXME: workaround solution, findByCode method in catalogService returns the catalog with stores==null, so NullPointerException throws
		// during import, therefore we must provide some additional logic to load stores in catalog
		if (catalog != null) {
			FetchGroupLoadTuner tuner = new FetchGroupLoadTunerImpl();
			tuner.addFetchGroup(FetchGroupConstants.CATALOG_EDITOR);
			catalog = catalogService.load(catalog.getUidPk(), tuner, false);
		}
		return catalog;
	}

	@Override
	protected void setImportStatus(final CatalogDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCode() + ")");
	}

	@Override
	protected String getDtoGuid(final CatalogDTO dto) {
		return dto.getCode();
	}

	@Override
	public boolean executeImport(final CatalogDTO object) {
		sanityCheck();

		setImportStatus(object);

		final Catalog obtainedCatalog = findPersistentObject(object);
		checkDuplicateGuids(object, obtainedCatalog);
		final Catalog catalog = getSavingStrategy().populateAndSaveObject(obtainedCatalog, object);

		// if catalog == null it means that this catalog was not imported because of import strategies reasons
		if (catalog != null) {
			// Imports the dependent data
			final LifecycleListener lifecycleListener = new DefaultLifecycleListener() {

				@Override
				public void beforeSave(final Persistable persistable) {
					((CatalogObject) persistable).setCatalog(catalog);
				}

				@Override
				public void beforePopulate(final Persistable persistable) {
					CatalogObject catalogObject = (CatalogObject) persistable;
					if (catalogObject.getCatalog() == null) {
						catalogObject.setCatalog(catalog);
					}
				}
			};

			// should be saved first because product types and category types have references to attributes and sku options
			saveAttributes(object, catalog, lifecycleListener, attributeSavingStrategy);
			saveSkuOptions(object, catalog, skuOptionSavingStrategy);

			saveBrands(object, catalog, this.<Brand, BrandDTO> createDefaultSavingStrategy(lifecycleListener));
			saveProductTypes(object, catalog, this.<ProductType, ProductTypeDTO> createDefaultSavingStrategy(lifecycleListener));
			saveCategoryTypes(object, catalog, this.<CategoryType, CategoryTypeDTO> createDefaultSavingStrategy(lifecycleListener));
			saveSynonymGroups(object, this.<SynonymGroup, SynonymGroupDTO> createDefaultSavingStrategy(lifecycleListener));
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T extends Persistable, K extends Dto> SavingStrategy<T, K> createDefaultSavingStrategy(final LifecycleListener listener) {
		SavingStrategy<T, K> savingStrategy = (SavingStrategy<T, K>) AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE,
				commonSavingManager);
		savingStrategy.setLifecycleListener(listener);
		return savingStrategy;
	}

	private void saveAttributes(final CatalogDTO object, final Catalog catalog, final LifecycleListener lifecycleListener,
			final SavingStrategy<Attribute, AttributeDTO> savingStrategy) {
		savingStrategy.setLifecycleListener(lifecycleListener);
		for (AttributeDTO attributeDTO : object.getAttributes()) {
			Attribute attribute = attributeService.findByKey(attributeDTO.getKey());

			if (isIncorrectCatalogObject(catalog, attribute) && !attribute.isGlobal()) {
				String code = null;
				if (attribute.getCatalog() != null) {
					code = attribute.getCatalog().getCode();
				}
				throw new PopulationRollbackException("IE-30600", attributeDTO.getKey(), code);
			}

			savingStrategy.populateAndSaveObject(attribute, attributeDTO);
		}
	}

	private void saveSkuOptions(final CatalogDTO object, final Catalog catalog, final SavingStrategy<SkuOption, SkuOptionDTO> savingStrategy) {
		// FIXME: SkuOption must be CatalogObject, when it does kill this code
		final LifecycleListener dummyForSkuOptionListener = new DefaultLifecycleListener() {
			@Override
			public void beforeSave(final Persistable persistable) {
				((SkuOption) persistable).setCatalog(catalog);
			}

			@Override
			public void beforePopulate(final Persistable persistable) {
				SkuOption skuOption = (SkuOption) persistable;
				if (skuOption.getCatalog() == null) {
					skuOption.setCatalog(catalog);
				}
			}
		};

		savingStrategy.setLifecycleListener(dummyForSkuOptionListener);
		// end of FixMe...

		for (SkuOptionDTO skuOptionDTO : object.getSkuOptions()) {
			SkuOption skuOption = skuOptionService.findByKey(skuOptionDTO.getCode());

			if (skuOption != null && !catalog.equals(skuOption.getCatalog())) {
				LOG.warn(new Message("IE-30601", skuOption.getGuid(), skuOption.getCatalog().getCode()));
				continue;
			}

			savingStrategy.populateAndSaveObject(skuOption, skuOptionDTO);
		}
	}

	private void saveProductTypes(final CatalogDTO object, final Catalog catalog,
			final SavingStrategy<ProductType, ProductTypeDTO> savingStrategy) {
		savingStrategy.setDomainAdapter(productTypeAdapter);

		for (ProductTypeDTO productTypeDTO : object.getProductTypes()) {
			ProductType productType = productTypeService.findProductType(productTypeDTO.getName());

			if (isIncorrectCatalogObject(catalog, productType)) {
				LOG.warn(new Message("IE-30602", productTypeDTO.getName(), productType.getCatalog().getCode()));
				continue;
			}

			savingStrategy.populateAndSaveObject(productType, productTypeDTO);
		}
	}

	private void saveSynonymGroups(final CatalogDTO object, final SavingStrategy<SynonymGroup, SynonymGroupDTO> savingStrategy) {
		if (object.getSynonymGroups() == null) {
			return;
		}

		savingStrategy.setDomainAdapter(synonymGroupAdapter);

		for (SynonymGroupDTO sysnonymGroupDTO : object.getSynonymGroups()) {
			savingStrategy.populateAndSaveObject(synonymGroupAdapter.createDomainObject(), sysnonymGroupDTO);
		}
	}

	private void saveCategoryTypes(final CatalogDTO object, final Catalog catalog,
			final SavingStrategy<CategoryType, CategoryTypeDTO> savingStrategy) {
		savingStrategy.setDomainAdapter(categoryTypeAdapter);

		for (CategoryTypeDTO categoryTypeDTO : object.getCategoryTypes()) {
			CategoryType categoryType = categoryTypeService.findCategoryType(categoryTypeDTO.getName());

			if (isIncorrectCatalogObject(catalog, categoryType)) {
				LOG.warn(new Message("IE-30603", categoryTypeDTO.getName(), categoryType.getCatalog().getCode()));
				continue;
			}

			savingStrategy.populateAndSaveObject(categoryType, categoryTypeDTO);
		}
	}

	private void saveBrands(final CatalogDTO object, final Catalog catalog, final SavingStrategy<Brand, BrandDTO> savingStrategy) {
		savingStrategy.setDomainAdapter(brandAdapter);

		for (BrandDTO brandDTO : object.getBrands()) {
			Brand brand = brandService.findByCode(brandDTO.getCode());

			if (isIncorrectCatalogObject(catalog, brand)) {
				LOG.warn(new Message("IE-30604", brandDTO.getCode(), brand.getCatalog().getCode()));
				continue;
			}

			savingStrategy.populateAndSaveObject(brand, brandDTO);
		}
	}

	@Override
	protected DomainAdapter<Catalog, CatalogDTO> getDomainAdapter() {
		return catalogAdapter;
	}

	private boolean isIncorrectCatalogObject(final Catalog catalog, final CatalogObject catalogObject) {
		return catalogObject != null && !catalog.equals(catalogObject.getCatalog());
	}

	@Override
	public String getImportedObjectName() {
		return CatalogDTO.ROOT_ELEMENT;
	}

	/**
	 * Gets the catalog service.
	 * 
	 * @return the catalogService
	 */
	public CatalogService getCatalogService() {
		return catalogService;
	}

	/**
	 * Sets the catalog service.
	 * 
	 * @param catalogService the catalogService to set
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * Gets the catalogAdapter.
	 * 
	 * @return the catalogAdapter
	 */
	public CatalogAdapter getCatalogAdapter() {
		return catalogAdapter;
	}

	/**
	 * Sets the catalogAdapter.
	 * 
	 * @param catalogAdapter the catalogAdapter to set
	 */
	public void setCatalogAdapter(final CatalogAdapter catalogAdapter) {
		this.catalogAdapter = catalogAdapter;
	}

	/**
	 * Sets the brandService.
	 * 
	 * @param brandService the brandService to set
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	/**
	 * Gets the brandService.
	 * 
	 * @return the brandService
	 */
	public BrandService getBrandService() {
		return brandService;
	}

	/**
	 * Gets the brandAdapter.
	 * 
	 * @return the brandAdapter
	 */
	public BrandAdapter getBrandAdapter() {
		return brandAdapter;
	}

	/**
	 * Sets the brandAdapter.
	 * 
	 * @param brandAdapter the brandAdapter to set
	 */
	public void setBrandAdapter(final BrandAdapter brandAdapter) {
		this.brandAdapter = brandAdapter;
	}

	/**
	 * Gets the cachingService.
	 * 
	 * @return the cachingService
	 */
	public CachingService getCachingService() {
		return cachingService;
	}

	/**
	 * Sets the cachingService.
	 * 
	 * @param cachingService the cachingService to set
	 */
	public void setCachingService(final CachingService cachingService) {
		this.cachingService = cachingService;
	}

	/**
	 * Gets the categoryTypeService.
	 * 
	 * @return the categoryTypeService
	 */
	public CategoryTypeService getCategoryTypeService() {
		return categoryTypeService;
	}

	/**
	 * Sets the categoryTypeService.
	 * 
	 * @param categoryTypeService the categoryTypeService to set
	 */
	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}

	/**
	 * Gets the categoryTypeAdapter.
	 * 
	 * @return the categoryTypeAdapter
	 */
	public CategoryTypeAdapter getCategoryTypeAdapter() {
		return categoryTypeAdapter;
	}

	/**
	 * Sets the categoryTypeAdapter.
	 * 
	 * @param categoryTypeAdapter the categoryTypeAdapter to set
	 */
	public void setCategoryTypeAdapter(final CategoryTypeAdapter categoryTypeAdapter) {
		this.categoryTypeAdapter = categoryTypeAdapter;
	}

	/**
	 * Gets the attributeService.
	 * 
	 * @return the attributeService
	 */
	public AttributeService getAttributeService() {
		return attributeService;
	}

	/**
	 * Sets the attributeService.
	 * 
	 * @param attributeService the attributeService to set
	 */
	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * Gets the attributeAdapter.
	 * 
	 * @return the attributeAdapter
	 */
	public AttributeAdapter getAttributeAdapter() {
		return attributeAdapter;
	}

	/**
	 * Sets the attributeAdapter.
	 * 
	 * @param attributeAdapter the attributeAdapter to set
	 */
	public void setAttributeAdapter(final AttributeAdapter attributeAdapter) {
		this.attributeAdapter = attributeAdapter;
	}

	/**
	 * Gets productTypeAdapter.
	 * 
	 * @return the productTypeAdapter
	 */
	public ProductTypeAdapter getProductTypeAdapter() {
		return productTypeAdapter;
	}

	/**
	 * Sets productTypeAdapter.
	 * 
	 * @param productTypeAdapter the productTypeAdapter to set
	 */
	public void setProductTypeAdapter(final ProductTypeAdapter productTypeAdapter) {
		this.productTypeAdapter = productTypeAdapter;
	}

	/**
	 * Gets productTypeService.
	 * 
	 * @return the productTypeService
	 */
	public ProductTypeService getProductTypeService() {
		return productTypeService;
	}

	/**
	 * Sets productTypeService.
	 * 
	 * @param productTypeService the productTypeService to set
	 */
	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	/**
	 * Gets skuOptionAdapter.
	 * 
	 * @return the skuOptionAdapter
	 */
	public SkuOptionAdapter getSkuOptionAdapter() {
		return skuOptionAdapter;
	}

	/**
	 * Sets skuOptionAdapter.
	 * 
	 * @param skuOptionAdapter the skuOptionAdapter to set
	 */
	public void setSkuOptionAdapter(final SkuOptionAdapter skuOptionAdapter) {
		this.skuOptionAdapter = skuOptionAdapter;
	}

	/**
	 * Gets skuOptionService.
	 * 
	 * @return the skuOptionService
	 */
	public SkuOptionService getSkuOptionService() {
		return skuOptionService;
	}

	/**
	 * Sets skuOptionService.
	 * 
	 * @param skuOptionService the skuOptionService to set
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

	/**
	 * Gets synonymGroupAdapter.
	 * 
	 * @return the synonymGroupAdapter
	 */
	public SynonymGroupAdapter getSynonymGroupAdapter() {
		return synonymGroupAdapter;
	}

	/**
	 * Sets synonymGroupAdapter.
	 * 
	 * @param synonymGroupAdapter the synonymGroupAdapter to set
	 */
	public void setSynonymGroupAdapter(final SynonymGroupAdapter synonymGroupAdapter) {
		this.synonymGroupAdapter = synonymGroupAdapter;
	}

	/**
	 * Gets synonymGroupService.
	 * 
	 * @return the synonymGroupService
	 */
	public SynonymGroupService getSynonymGroupService() {
		return synonymGroupService;
	}

	/**
	 * Sets synonymGroupService.
	 * 
	 * @param synonymGroupService the synonymGroupService to set
	 */
	public void setSynonymGroupService(final SynonymGroupService synonymGroupService) {
		this.synonymGroupService = synonymGroupService;
	}

	/**
	 * @inheritDoc
	 * @return Some CollectionsStrategy.
	 */
	@Override
	@SuppressWarnings("PMD.CyclomaticComplexity")
	protected CollectionsStrategy<Catalog, CatalogDTO> getCollectionsStrategy() {
		return new CollectionsStrategy<Catalog, CatalogDTO>() {
			@Override
			public boolean isForPersistentObjectsOnly() {
				return true;
			}

			@Override
			public void prepareCollections(final Catalog domainObject, final CatalogDTO dto) {
				// FIXME: problem with jpa, if we call setSupportedLocales method than all locales disappear from DB, the same behaviour with
				// currency
				// useClearCollectionForLocales(domainObject, dto);
			}

			@SuppressWarnings("unused")
			private void useClearCollectionForLocales(final Catalog domainObject, final CatalogDTO dto) {
				Collection<Locale> supportedLocales = domainObject.getSupportedLocales();
				List<String> languages = dto.getLanguages();

				Collection<Locale> newSupportedLocales = new HashSet<Locale>();
				for (Locale locale : supportedLocales) {
					if (findLocaleInDTO(languages, locale)) {
						newSupportedLocales.add(locale);
					}
				}

				newSupportedLocales.add(domainObject.getDefaultLocale());

				try {
					domainObject.setSupportedLocales(newSupportedLocales);
				} catch (DefaultValueRemovalForbiddenException e) {
					LOG.warn(new Message("IE-30605", e));
				}
			}

			private boolean findLocaleInDTO(final List<String> languages, final Locale locale) {
				for (String language : languages) {
					// TODO this may cause problems, perhaps it's better to call toString() rather then getLanguage()
					if (language.equals(locale.getLanguage())) {
						return true;
					}
				}

				return false;
			}
		};
	}

	@Override
	public Class<? extends CatalogDTO> getDtoClass() {
		return CatalogDTO.class;
	}
}
