package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.category.CategoryAdapter;
import com.elasticpath.importexport.common.adapters.category.LinkedCategoryAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Category importer implementation.
 */
public class CategoryImporterImpl extends AbstractImporterImpl<Category, CategoryDTO> { // NOPMD
	
	private static final Logger LOG = Logger.getLogger(CategoryImporterImpl.class);

	private CategoryAdapter categoryAdapter;

	private LinkedCategoryAdapter linkedCategoryAdapter;

	private CategoryService categoryService;

	private CachingService cachingService;

	private SavingStrategy<Category, LinkedCategoryDTO> linkedCategorySavingStrategy;

	private final Map<String, Set<Integer> > orderingMap = new HashMap<String, Set<Integer>>();

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Category, CategoryDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		orderingMap.clear();

		getSavingStrategy().setSavingManager(new SavingManager<Category>() {
			
			@Override
			public Category update(final Category persistable) {
				return categoryService.saveOrUpdate(persistable);
			}

			@Override
			public void save(final Category persistable) {
				update(persistable);
			}

		});

		linkedCategorySavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE, getSavingStrategy()
				.getSavingManager());
		linkedCategorySavingStrategy.setDomainAdapter(linkedCategoryAdapter);
	}

	@Override
	public boolean executeImport(final CategoryDTO object) { // NOPMD
		sanityCheck();
		orderingCheck(object);

		setImportStatus(object);
		
		final Category obtainedCategory = findPersistentObject(object);
		checkDuplicateGuids(object, obtainedCategory);
		
		final Category category = getSavingStrategy().populateAndSaveObject(obtainedCategory, object);

		if (category != null) {
			linkedCategorySavingStrategy.setLifecycleListener(new DefaultLifecycleListener() {

				@Override
				public void beforeSave(final Persistable persistable) {
					((Category) persistable).setMasterCategory(category);
				}

				@Override
				public void afterSave(final Persistable persistable) {
				 	Category category = (Category) persistable;
				 	if (category.getParent() == null) {
				 		return;
				 	}
				}

			});

			List<LinkedCategoryDTO> linkedCategoryDTOList = object.getLinkedCategoryDTOList();

			for (LinkedCategoryDTO linkedCategoryDTO : linkedCategoryDTOList) {
				Catalog catalog = getCachingService().findCatalogByCode(linkedCategoryDTO.getVirtualCatalogCode());
				if (catalog == null) {
					throw new PopulationRollbackException("IE-30701", object.getCategoryCode(), linkedCategoryDTO.getVirtualCatalogCode());
				}
				if (catalog.isMaster()) {
					throw new PopulationRollbackException("IE-30702", object.getCategoryCode(), linkedCategoryDTO.getVirtualCatalogCode());
				}
				Category linkedCategory = categoryService.findByGuid(object.getCategoryCode(), linkedCategoryDTO.getVirtualCatalogCode());
				long catalogUid = catalog.getUidPk();

				Category linkedParent = null;
				if (category.getParent() != null) {
					linkedParent = categoryService.findByGuid(category.getParent().getCode(), linkedCategoryDTO.getVirtualCatalogCode());
				}

				if (linkedParent == null) {
					linkedParent = categoryAdapter.createDomainObject();
					linkedParent.setUidPk(-1L);
				}

				category.setOrdering(linkedCategoryDTO.getOrder());
				if (linkedCategory == null) {
					try {
						categoryService.addLinkedCategory(category.getUidPk(), linkedParent.getUidPk(), catalogUid);
					} catch (DuplicateKeyException exception) {
						throw new PopulationRollbackException("IE-30703", exception, category.getCode(), catalog.getCode(), exception.getMessage());
					}
				} else {
					linkedCategorySavingStrategy.populateAndSaveObject(linkedCategory, linkedCategoryDTO);
				}
			}
			return true;
		}
		return false;
	}
	
	private void orderingCheck(final CategoryDTO object) {
		final String parentCategoryCode = object.getParentCategoryCode();
		if (orderingMap.containsKey(parentCategoryCode)) {
			if (orderingMap.get(parentCategoryCode).contains(object.getOrder())) {
				LOG.warn(new Message("IE-30700", object.getCategoryCode()));
			}				
		} else {				
			orderingMap.put(parentCategoryCode, new HashSet<Integer>());
		}

		orderingMap.get(parentCategoryCode).add(object.getOrder());
	}

	@Override
	protected String getDtoGuid(final CategoryDTO dto) {
		return dto.getCategoryCode();
	}

	@Override
	protected Category findPersistentObject(final CategoryDTO dto) {
		Category category = cachingService.findCategoryByCode(dto.getCategoryCode(), dto.getCatalogCode());
		if (category == null) {
			return null;
		}
		return categoryService.get(category.getUidPk());
	}
	
	@Override
	protected void setImportStatus(final CategoryDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCategoryCode() + ")");
	}

	@Override
	protected DomainAdapter<Category, CategoryDTO> getDomainAdapter() {
		return categoryAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return CategoryDTO.ROOT_ELEMENT;
	}

	@Override
	protected CollectionsStrategy<Category, CategoryDTO> getCollectionsStrategy() {
		return new CategoryCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.CATEGORY));
	}

	/**
	 * Gets the categoryAdapter.
	 * 
	 * @return the categoryAdapter
	 */
	public CategoryAdapter getCategoryAdapter() {
		return categoryAdapter;
	}

	/**
	 * Sets the categoryAdapter.
	 * 
	 * @param categoryAdapter the categoryAdapter to set
	 */
	public void setCategoryAdapter(final CategoryAdapter categoryAdapter) {
		this.categoryAdapter = categoryAdapter;
	}

	/**
	 * Gets the linkedCategoryAdapter.
	 * 
	 * @return the linkedCategoryAdapter
	 */
	public LinkedCategoryAdapter getLinkedCategoryAdapter() {
		return linkedCategoryAdapter;
	}

	/**
	 * Sets the linkedCategoryAdapter.
	 * 
	 * @param linkedCategoryAdapter the linkedCategoryAdapter to set
	 */
	public void setLinkedCategoryAdapter(final LinkedCategoryAdapter linkedCategoryAdapter) {
		this.linkedCategoryAdapter = linkedCategoryAdapter;
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
	 * Gets the categoryService.
	 * 
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * Sets the categoryService.
	 * 
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Override
	public Class<? extends CategoryDTO> getDtoClass() {
		return CategoryDTO.class;
	}

	/**
	 * Collections strategy for category object.
	 */
	private final class CategoryCollectionsStrategy implements CollectionsStrategy<Category, CategoryDTO> {

		private final boolean isAttributeClearStrategy;

		public CategoryCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			isAttributeClearStrategy = importerConfiguration.getCollectionStrategyType(DependentElementType.CATEGORY_ATTRIBUTES).equals(
					CollectionStrategyType.CLEAR_COLLECTION);
		}

		@Override
		public void prepareCollections(final Category domainObject, final CategoryDTO dto) {
			if (isAttributeClearStrategy) {
				domainObject.getAttributeValueMap().clear();
			}
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}
}
