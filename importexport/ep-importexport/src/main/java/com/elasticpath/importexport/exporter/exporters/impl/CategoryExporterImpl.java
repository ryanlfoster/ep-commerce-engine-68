package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.assets.AssetFileManager;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Exporter implementation for category object.
 */
public class CategoryExporterImpl extends AbstractExporterImpl<Category, CategoryDTO, Long> {

	private CategoryService categoryService;

	private CategoryLoadTuner categoryLoadTuner;

	private DomainAdapter<Category, CategoryDTO> categoryAdapter;

	private List<Long> categoryUidsFromSearchCriteria = Collections.emptyList();

	private ImportExportSearcher importExportSearcher;

	private List<Long> categoryUidsList = Collections.emptyList();
	
	private ApplicationPropertiesHelper applicationPropertiesHelper;

	private static final Logger LOG = Logger.getLogger(CategoryExporterImpl.class);

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		categoryUidsFromSearchCriteria = importExportSearcher.searchUids(context.getSearchConfiguration(), EPQueryType.CATEGORY);
		LOG.info("The UidPk list for " + categoryUidsFromSearchCriteria.size() + " categories is retrieved from database.");
	}

	@Override
	protected List<Category> findByIDs(final List<Long> subList) {
		List<Category> categoryList = new ArrayList<Category>();
		for (Long categoryUid : subList) {
			Category category = categoryService.load(categoryUid, categoryLoadTuner);
			if (category == null) {
				LOG.error(new Message("IE-20700", categoryUid.toString()));
				continue;
			}

			if (category.isLinked()) {
				processLinkedCategory(category);
				continue;
			}

			categoryList.add(category);

		}
		return categoryList;
	}

	private void processLinkedCategory(final Category linkedCategory) {
		Category masterCategory = linkedCategory.getMasterCategory();
		if (!categoryUidsList.contains(masterCategory.getUidPk())) {
			Set<Long> linkedAncestorUids = categoryService.findAncestorCategoryUidsWithTreeOrder(Collections.singleton(masterCategory.getUidPk()));

			for (Long ancestorUid : linkedAncestorUids) {
				if (!categoryUidsList.contains(ancestorUid)) {
					categoryUidsList.add(ancestorUid);
				}
			}

			categoryUidsList.add(masterCategory.getUidPk());
		}
	}

	@Override
	protected DomainAdapter<Category, CategoryDTO> getDomainAdapter() {
		return categoryAdapter;
	}

	@Override
	protected Class<? extends CategoryDTO> getDtoClass() {
		return CategoryDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.CATEGORY;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		Set<Long> categoryUidsSet = new HashSet<Long>(getContext().getDependencyRegistry().getDependentUids(Category.class));
		categoryUidsSet.addAll(categoryUidsFromSearchCriteria);

		// find all parent category uids in necessary tree order
		Set<Long> resultSet = categoryService.findAncestorCategoryUidsWithTreeOrder(categoryUidsSet);

		// add other category uids for export
		resultSet.addAll(categoryUidsSet);

		categoryUidsList = new ArrayList<Long>(Arrays.asList(resultSet.toArray(new Long[resultSet.size()])));
		return categoryUidsList;
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] { Category.class };
	}

	@Override
	protected void addDependencies(final List<Category> objects, final DependencyRegistry dependencyRegistry) {

		if (dependencyRegistry.supportsDependency(Catalog.class)) {
			addCatalogsIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(CategoryType.class)) {
			addCategoryTypesIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(Attribute.class)) {
			addAttributesIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(DigitalAsset.class)) {
			addAssetsIntoRegistry(objects, dependencyRegistry);
		}

	}

	private void addAssetsIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {

		for (Category category : categories) {
			Map<Integer, List<String>> categoryAttributeValues = getCategoryAttributeValuesByType(category);

			final String imagesFolder = getImageAssetSubFolder();
			addAssetFilesIntoRegistry(imagesFolder, categoryAttributeValues.get(AttributeType.IMAGE_TYPE_ID), dependencyRegistry);

			final String digitalAssetsFolder = getSecureAssetSubFolder();
			addAssetFilesIntoRegistry(digitalAssetsFolder, categoryAttributeValues.get(AttributeType.FILE_TYPE_ID), dependencyRegistry);
		}

	}

	private void addAssetFilesIntoRegistry(final String folder, final List<String> filenames, final DependencyRegistry dependencyRegistry) {
		for (String filename : filenames) {
			dependencyRegistry.addAsset(folder, filename);
		}
	}

	/* Get attribute type of category by attribyteTypes list and return attribute values. */
	private Map<Integer, List<String>> getCategoryAttributeValuesByType(final Category category) {
		Map<Integer, List<String>> categoryAttributeValues = new HashMap<Integer, List<String>>();
		for (Integer typeId : getAssetsAttributeTypes()) {
			categoryAttributeValues.put(typeId, new ArrayList<String>());
		}
		Collection<AttributeValue> attributeValues = category.getAttributeValueMap().values();
		for (AttributeValue attributeValue : attributeValues) {
			int typeId = attributeValue.getAttributeType().getTypeId();
			if (getAssetsAttributeTypes().contains(typeId) && attributeValue.getValue() != null) {
				categoryAttributeValues.get(typeId).add(attributeValue.getStringValue());
			}
		}
		return categoryAttributeValues;
	}

	private String getImageAssetSubFolder() {
		return getApplicationPropertiesHelper().getPropertiesWithNameStartsWith("asset").get(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER);
	}

	private String getSecureAssetSubFolder() {
		return getApplicationPropertiesHelper().getPropertiesWithNameStartsWith("asset").get(AssetFileManager.PROPERTY_DIGITALGOODS_ASSET_SUBFOLDER);
	}

	private void addAttributesIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {
		final Set<Long> dependents = new HashSet<Long>();
		for (Category category : categories) {
			for (Entry<String, AttributeValue> entry : category.getAttributeValueMap().entrySet()) {
				dependents.add(entry.getValue().getAttribute().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(Attribute.class, dependents);
	}

	private void addCategoryTypesIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {
		final Set<Long> dependents = new HashSet<Long>();
		for (Category category : categories) {
			if (category.getCategoryType() != null) {
				dependents.add(category.getCategoryType().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(CategoryType.class, dependents);
	}

	/*
	 * Puts directly influencing catalogs, as well as indirect (catalog containing exported category is virtual).
	 */
	private void addCatalogsIntoRegistry(final List<Category> objects, final DependencyRegistry dependencyRegistry) {
		final VirtualCatalogDependencyHelper virtualDependencyHelper = new VirtualCatalogDependencyHelper();
		final Set<Long> catalogSetUid = new HashSet<Long>();
		for (Category category : objects) {
			catalogSetUid.add(category.getCatalog().getUidPk());
			virtualDependencyHelper.addInfluencingCatalogs(category, dependencyRegistry);
		}
		dependencyRegistry.addUidDependencies(Catalog.class, catalogSetUid);
	}

	private List<Integer> getAssetsAttributeTypes() {
		List<Integer> assetsAttributeTypes = new ArrayList<Integer>();
		assetsAttributeTypes.add(AttributeType.FILE_TYPE_ID);
		assetsAttributeTypes.add(AttributeType.IMAGE_TYPE_ID);
		return assetsAttributeTypes;
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

	/**
	 * Gets the categoryLoadTuner.
	 *
	 * @return the categoryLoadTuner
	 */
	public CategoryLoadTuner getCategoryLoadTuner() {
		return categoryLoadTuner;
	}

	/**
	 * Sets the categoryLoadTuner.
	 *
	 * @param categoryLoadTuner the categoryLoadTuner to set
	 */
	public void setCategoryLoadTuner(final CategoryLoadTuner categoryLoadTuner) {
		this.categoryLoadTuner = categoryLoadTuner;
	}

	/**
	 * Sets the categoryAdapter.
	 *
	 * @param categoryAdapter the categoryAdapter to set
	 */
	public void setCategoryAdapter(final DomainAdapter<Category, CategoryDTO> categoryAdapter) {
		this.categoryAdapter = categoryAdapter;
	}

	/**
	 * Gets importExportSearcher.
	 *
	 * @return importExportSearcher
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * Sets importExportSearcher.
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	protected ApplicationPropertiesHelper getApplicationPropertiesHelper() {
		return applicationPropertiesHelper;
	}

	public void setApplicationPropertiesHelper(final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}
}
