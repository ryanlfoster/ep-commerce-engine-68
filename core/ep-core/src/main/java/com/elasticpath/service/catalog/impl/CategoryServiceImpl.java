/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.commons.exception.EpCategoryNotEmptyException;
import com.elasticpath.commons.exception.IllegalOperationException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryDeleted;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * The default implementation of <code>CategoryService</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.TooManyMethods" })
public class CategoryServiceImpl extends AbstractEpPersistenceServiceImpl implements CategoryService {

	private static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class);

	private static final String PLACE_HOLDER_FOR_LIST = "list";

	private CategoryLoadTuner categoryLoadTunerAll;

	private CategoryLoadTuner categoryLoadTunerAllDeep;

	private CategoryLoadTuner categoryLoadTunerMinimal;

	private CategoryLoadTuner categoryLoadTunerDefault;

	private FetchPlanHelper fetchPlanHelper;

	private ProductService productService;

	private CatalogService catalogService;

	private FetchGroupLoadTuner defaultFetchGroupLoadTuner;

	private ProductDao productDao;

	/** Load tuner used in operations related to linking products to categories or linked categories. */
	private FetchGroupLoadTuner linkProductCategoryLoadTuner;

	private static final String DUPLICATE_GUID = "Inconsistent data -- duplicate guid:";

	/**
	 * Adds the given category.
	 * This implementation must first check whether a category with the given code already exists, since
	 * master categories and linked categories can share the same code but are in fact different (there
	 * can be multiple linked categories with the same code as long as they're in different catalogs, and
	 * linked categories share the same code as their master categories).
	 *
	 * Calls {@link #masterCategoryExists(String)} and {@link #linkedCategoryExists(String, String)}.
	 *
	 * @param category the category to add
	 * @return the persisted instance of category
	 * @throws EpServiceException - in case of any errors
	 * @throws DuplicateKeyException if a category with the given code already exists
	 */
	@Override
	public Category add(final Category category) throws EpServiceException {
		sanityCheck();
		if ((category.isLinked() && linkedCategoryExists(category.getCode(), category.getCatalog().getCode()))
				|| (!category.isLinked() && masterCategoryExists(category.getCode()))) {
			throw new DuplicateKeyException(
					"Category code '" + category.getCode() + "' already exists in catalog " + category.getCatalog().getCode());
		}
		getPersistenceEngine().save(category);
		getProductService().notifyCategoryUpdated(category);
		getIndexNotificationService().addNotificationForEntityIndexUpdate(IndexType.CATEGORY, category.getUidPk());
		return category;
	}

	/**
	 * Checks whether a master category with the given category code exists in the database.
	 * @param categoryCode the code to search for.
	 * @return true if a category with the given code exists, false if not
	 */
	protected boolean masterCategoryExists(final String categoryCode) {
		if (getPersistenceEngine().<Long>retrieveByNamedQuery("CATEGORY_COUNT_BY_CODE", categoryCode).get(0) >= 1) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether a linked category with the given code already exists in the catalog
	 * represented by the given catalog code.
	 * @param categoryCode the linked category code, or the code for the master category to which the
	 * linked category belongs, since linked categories and their master categories use the same code.
	 * @param catalogCode the code for the catalog in which to check for the existence of the linked category code
	 * @return true if it exists, false if not
	 */
	protected boolean linkedCategoryExists(final String categoryCode, final String catalogCode) {
		if (getPersistenceEngine().<Long>retrieveByNamedQuery("LINKED_CATEGORY_COUNT_BY_CODE", categoryCode, catalogCode).get(0) >= 1) {
			return true;
		}
		return false;
	}

	/**
	 * Get the top-level catalog for the given category. If the category is
	 * a linked category then its master category's catalog will be obtained.
	 * @param category the category for which to retrieve the master catalog
	 * @return the requested catalog, or null if one cannot be found
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of error
	 */
	@Override
	public Catalog getMasterCatalog(final Category category) {
		List<Catalog> catalogList;
		if (category.isLinked()) {
			catalogList = getPersistenceEngine().<Catalog> retrieveByNamedQuery(
					"FIND_MASTER_CATALOG_FOR_LINKED_CATEGORY", category.getUidPk());
		} else {
			catalogList = getPersistenceEngine().<Catalog> retrieveByNamedQuery(
					"FIND_CATALOG_FOR_CATEGORY", category.getUidPk());
		}
		if (!catalogList.isEmpty()) {
			return catalogList.get(0);
		}
		return null;
	}

	/**
	 * Updates the given category.
	 *
	 * @param category the category to update
	 * @return the updated category instance
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category update(final Category category) throws EpServiceException {
		sanityCheck();
		final Category updatedCategory = getPersistenceEngine().update(category);
		getProductService().notifyCategoryUpdated(updatedCategory);
		getIndexNotificationService().addNotificationForEntityIndexUpdate(IndexType.CATEGORY, updatedCategory.getUidPk());
		return updatedCategory;
	}


	/**
	 * Loads the category with the given UID and recursively loads all it's children.
	 *
	 * @param categoryUid the category UID
	 * @return the category if ID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category loadWithAllChildren(final long categoryUid) throws EpServiceException {
		return load(categoryUid, categoryLoadTunerAllDeep);
	}

	/**
	 * Load the category with the given UID.
	 *
	 * @param categoryUid the category UID
	 * @return the category if ID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category load(final long categoryUid) throws EpServiceException {
		return load(categoryUid, categoryLoadTunerDefault);
	}

	/**
	 * Loads the category with the given UID.
	 *
	 * @param categoryUid the category UID
	 * @param loadTuner the load tuner to use, or <code>null</code> to not use a load tuner
	 * @return the category if it exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Category load(final long categoryUid, final CategoryLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		Category category = null;
		if (categoryUid <= 0) {
			return getBean(ContextIdNames.CATEGORY);
		}

		fetchPlanHelper.configureCategoryFetchPlan(loadTuner);
		category = getPersistenceEngine().load(getAbstractCategoryImplClass(), categoryUid);
		fetchPlanHelper.clearFetchPlan();
		return category;
	}

	/**
	 * Extension point to override the parent class for categories, the return
	 * value of this is passed to persistenceEngine methods load() and get().
	 * To override the category implementations this should prove useful
	 * @return the class that is the parent of linked and master categories.
	 */
	protected Class< ? extends Category> getAbstractCategoryImplClass() {
		return getElasticPath().getBeanImplClass(ContextIdNames.ABSTRACT_CATEGORY);
	}

	/**
	 * Loads the category with the given UID.
	 *
	 * @param categoryUid the category UID
	 * @param loadTuner the load tuner to use, or <code>null</code> to not use a load tuner
	 * @return the category if it exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Category load(final long categoryUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		Category category = null;
		if (categoryUid <= 0) {
			return getBean(ContextIdNames.CATEGORY);
		}

		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		category = getPersistenceEngine().load(getAbstractCategoryImplClass(), categoryUid);
		fetchPlanHelper.clearFetchPlan();
		return category;
	}

	/**
	 * Get the category with the given UID. Return null if no matching record exists. This also populates
	 *
	 * @param categoryUid the category UID
	 * @return the category if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category get(final long categoryUid) throws EpServiceException {
 		return load(categoryUid, categoryLoadTunerAll);
	}

	/**
	 * Get the category with the given UID. Return null if no matching record exists.
	 *
	 * @param categoryUid the category UID
	 * @return the category with attribute populated if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category getCategoryWithAttribute(final long categoryUid) throws EpServiceException {
		return load(categoryUid, categoryLoadTunerAll);
	}

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance UID
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Retrieve a list of root categories.
	 *
	 * @param catalog the catalog to get the root categories for
	 * @param availableOnly set it to <code>true</code> to only list available root categories
	 * @return return all root categories
	 */
	@Override
	public List<Category> listRootCategories(final Catalog catalog, final boolean availableOnly) {
		return listRootCategories(catalog, availableOnly, null);
	}

	/**
	 * Retrieve all root categories.
	 *
	 * @param catalog the catalog to get the root categories for
	 * @param availableOnly set it to <code>true</code> to only list available root categories
	 * @param categoryLoadTuner the given fetch group load tuner used to load categories. If null is passed,
	 *            the default tuner will be used.
	 * @return return all root categories
	 */
	@Override
	public List<Category> listRootCategories(final Catalog catalog, final boolean availableOnly,
			final FetchGroupLoadTuner categoryLoadTuner) {
		if (catalog == null) {
			return Collections.emptyList();
		}

		sanityCheck();
		List<Category> readOnlyResultCategory;
		List<Category> readOnlyResultLinkedCategory = Collections.emptyList();

		FetchGroupLoadTuner loadTuner = categoryLoadTuner;
		if (loadTuner == null) {
			loadTuner = getDefaultFetchGroupLoadTuner();
		}

		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		if (availableOnly) {
			final Date now = new Date();
			readOnlyResultCategory = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_LIST_AVAILABLE_ROOT", now, now, catalog.getUidPk());
			readOnlyResultLinkedCategory = getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_LIST_AVAILABLE_ROOT",
					now,
					now,
					catalog.getUidPk());

		} else {
			readOnlyResultCategory = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_LIST_ROOT", catalog.getUidPk());
		}
		fetchPlanHelper.clearFetchPlan();

		final List<Category> result = new ArrayList<Category>(readOnlyResultCategory.size() + readOnlyResultLinkedCategory.size());
		result.addAll(readOnlyResultCategory);
		result.addAll(readOnlyResultLinkedCategory);
		Collections.sort(result);
		return result;
	}

	/**
	 * Retrieve root categories with the entire tree of children loaded.
	 *
	 * @param catalog the catalog to get the root categories for
	 * @param availableOnly set it to <code>true</code> to only list available categories
	 * @return root categories for the given catalog with the entire tree of children categories
	 *         loaded
	 */
	@Override
	public List<Category> listRootCategoriesWithTree(final Catalog catalog, final boolean availableOnly) {
		return listRootCategoriesWithTree(catalog, availableOnly, null);
	}

	/**
	 * Retrieve root categories with the entire tree of children loaded using the given category
	 * load tuner.
	 *
	 * @param catalog the catalog to get the root categories for
	 * @param availableOnly set it to <code>true</code> to only list available categories
	 * @param categoryLoadTuner the given fetch group load tuner used to load categories. If <code>null</code>
	 *            is passed, the default tuner will be used.
	 * @return root categories for the given catalog with the entire tree of children categories
	 *         loaded
	 */
	@Override
	public List<Category> listRootCategoriesWithTree(final Catalog catalog, final boolean availableOnly,
			final FetchGroupLoadTuner categoryLoadTuner) {

		FetchGroupLoadTuner loadTuner = categoryLoadTuner;
		if (loadTuner == null) {
			loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			loadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_BASIC,
					FetchGroupConstants.CATALOG_DEFAULTS, // need default locale
					FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, // needed for SEO

					 // TODO: This will have performance issue, may need to refactor later.
					//Currently only the sitemap will load the categories with tree.
					FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH
			);
		}


		return listRootCategories(catalog, availableOnly, loadTuner);

	}

	/**
	 * Retrieves the {@link Category} with the given GUID in a particular catalog. The returned Category
	 * could be either linked or non-linked depending on the catalog.
	 *
	 * @param categoryGuid the GUId of the category
	 * @param catalogGuid the catalog to search in
	 * @return the category with the given GUID
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Category findByGuid(final String categoryGuid, final String catalogGuid) {
	    return findByGuid(categoryGuid, catalogGuid, categoryLoadTunerDefault);
	}

	/**
	 * Finds category by compound guid.
	 *
	 * @param compoundGuid compound guid (ex, "category_code|catalog_code")
	 * @return appropriate category or null if category with given compound does not exist
	 */
	@Override
	public Category findByCompoundGuid(final String compoundGuid) {
		final String categoryCode = compoundGuid.substring(0, compoundGuid.indexOf(Category.CATEGORY_GUID_DELIMITER));
		final String catalogCode = compoundGuid.substring(compoundGuid.indexOf(Category.CATEGORY_GUID_DELIMITER)
				+ Category.CATEGORY_GUID_DELIMITER.length());
		try {
			return findByGuid(categoryCode, catalogCode);
		} catch (final EpServiceException exception) {
			return null;
		}
	}


	/**
	 * Retrieve the {@link Category} with the given GUID in a particular catalog. The returned Category
	 * could be either linked or non-linked depending on the catalog.
	 *
	 * @param guid the GUID of the category
	 * @param catalog the catalog to search in
	 * @return the category with the given GUID
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Category findByGuid(final String guid, final Catalog catalog) {
		return findByGuid(guid, catalog, null);
	}

	/**
	 * Retrieve the {@link Category} with the given GUID.
	 *
	 * @param guid the GUID of the category
	 * @return the category with the given GUID
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Category findByGuid(final String guid) {
		sanityCheck();

		List<Category> categories;
		fetchPlanHelper.configureCategoryFetchPlan(categoryLoadTunerMinimal);
		categories = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_SELECT_BY_GUID", guid);

		fetchPlanHelper.clearFetchPlan();

		final int size = categories.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID + guid);
		} else if (size == 0) {
			return null;
		}
		return categories.get(0);
	}




	/**
	 * Retrieve the {@link Category} with the given GUID in a particular catalog. The returned
	 * Category could be either linked or non-linked depending on the catalog. Give a load tuner
	 * to tune the result or {@code null} to tune to the default.
	 *
	 * @param guid the GUID of the category
	 * @param catalog the catalog to search in
	 * @param loadTuner the load tuner to use
	 * @return the category with the given GUID
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Category findByGuid(final String guid, final Catalog catalog, final FetchGroupLoadTuner loadTuner) {
		if (catalog == null) {
			LOG.error("Attempt to find category with null catalog for category guid: " + guid);
			return null;
		}

		if (loadTuner == null) {
			fetchPlanHelper.configureCategoryFetchPlan(categoryLoadTunerDefault);
		} else {
			fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		}

		List<Category> categories = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_FIND_BY_GUID_CATALOG", guid, catalog.getUidPk());
		if (categories == null || categories.isEmpty()) {
			categories = getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_FIND_BY_GUID_CATALOG", guid, catalog.getUidPk());
		}

		fetchPlanHelper.clearFetchPlan();

		final int size = categories.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID + guid);
		} else if (size == 0) {
			return null;
		}
		return categories.get(0);
	}

	/**
	 * Save or update the given category.
	 *
	 * @param category the category to save or update
	 * @return the updated category
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category saveOrUpdate(final Category category) throws EpServiceException {
		final Category updatedCategory = getPersistenceEngine().saveOrMerge(category);

		// Manually evict the parent (if one exists) from the cache as the bi-directional parent-child relationship
		// confuses JPA slightly, and without a manual eviction the parent will remain cached without the new child.
		if (updatedCategory.getParent() != null) {
			getPersistenceEngine().evictObjectFromCache(updatedCategory.getParent());
		}

		getProductService().notifyCategoryUpdated(updatedCategory);
		getIndexNotificationService().addNotificationForEntityIndexUpdate(IndexType.CATEGORY, updatedCategory.getUidPk());
		return updatedCategory;
	}

	/**
	 * Retrieve the list of sub-categories of the current category.
	 *
	 * @param categoryUid the current category
	 * @return the list of sub-categories of the current category.
	 */
	@Override
	public List<Category> getSubCategories(final long categoryUid) {
		sanityCheck();
		List<Category> result;
		fetchPlanHelper.configureCategoryFetchPlan(categoryLoadTunerDefault);
		if (categoryUid > 0) {
			final Category category = getPersistenceEngine().load(getAbstractCategoryImplClass(), categoryUid);
			result = new ArrayList<Category>(category.getChildren());
		} else {
			result = Collections.emptyList();
		}
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	/**
	 * Query if the category has sub categories. For performance.
	 *
	 * @param categoryUid the category's uid
	 * @return true if the category has subcategories
	 */
	@Override
	public boolean hasSubCategories(final long categoryUid) {
		sanityCheck();
		if (getSubCategoryCount(categoryUid) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Retrieve all descendant category UIDs of the given category UID.
	 *
	 * @param categoryUid the category UID.
	 * @return the list of UID of the direct and indirect sub-categories of the given start
	 *         category.
	 */
	@Override
	public List<Long> findDescendantCategoryUids(final long categoryUid) {
		sanityCheck();
		List<Long> result = new ArrayList<Long>();
		if (categoryUid > 0) {
			final List<Long> queryResponse = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_LIST_SUBCATEGORY_UIDS", new Long(categoryUid));
			result.addAll(queryResponse);
		} else {
			result = Collections.emptyList();
		}

		if (result != null && !result.isEmpty()) {
			result.addAll(findDescendantCategoryUids(result));
		}
		return result;
	}

	@Override
	public List<Category> findDirectDescendantCategories(final long categoryUid) {
		sanityCheck();
		List<Category> result = new ArrayList<Category>();
		if (categoryUid > 0) {
			final List<Category> queryResponse = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_LIST_SUBCATEGORY", new Long(categoryUid));
			result.addAll(queryResponse);
		} else {
			result = Collections.emptyList();
		}

		Collections.sort(result);

		return result;
	}

	/**
	 * Retrieve all descendant category UIDs of the given category UIDs.
	 *
	 * @param categoryUids the category UIDs.
	 * @return the list of UIDs of the direct and indirect sub-categories of the given start
	 *         category UIDs.
	 */
	@Override
	public List<Long> findDescendantCategoryUids(final List<Long> categoryUids) {
		sanityCheck();
		final List<Long> result = new ArrayList<Long>();
		List<Long> subCategoryUids = getPersistenceEngine().retrieveByNamedQueryWithList(
				"CATEGORY_UID_SELECT_BY_PARENT_UIDS", PLACE_HOLDER_FOR_LIST, categoryUids);
		while (!subCategoryUids.isEmpty()) {
			result.addAll(subCategoryUids);
			subCategoryUids = getPersistenceEngine().retrieveByNamedQueryWithList("CATEGORY_UID_SELECT_BY_PARENT_UIDS", PLACE_HOLDER_FOR_LIST,
					subCategoryUids);
		}

		return result;
	}

	/**
	 * Return <code>true</code> if the product with the given product UID is in the category
	 * with the given category UID. Otherwise, <code>false</code>.
	 *
	 * @param productUid the product UID
	 * @param categoryUid the category UID
	 * @return <code>true</code> if the product with the given product UID is in the category
	 *         with the given category UID. Otherwise, <code>false</code>
	 */
	@Override
	public boolean isProductInCategory(final long productUid, final long categoryUid) {
		final List< ? > result = getPersistenceEngine().retrieveByNamedQuery("SELECT_PRODUCT_CATEGORY_ASSOCIATION",
				new Long(productUid), new Long(categoryUid));
		if (result.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Get the category with the given UID. Return null if no matching record exists. All
	 * sub-categories will also be populated. This method also populates attributes
	 *
	 * @param categoryUid the category UID
	 * @return the category if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Category getCategoryWithSubCategories(final long categoryUid) throws EpServiceException {
		return load(categoryUid, categoryLoadTunerDefault);
	}

	@Override
	public boolean hasProduct(final long categoryUid) throws EpServiceException {
		return getProductService().hasProductsInCategory(categoryUid);
	}

	/**
	 * Deletes the product category.
	 *
	 * @param categoryUid the category to be removed
	 * @throws EpServiceException - in case of any errors
	 */
	private void removeProductCategory(final long categoryUid) throws EpServiceException {
		sanityCheck();

		getPersistenceEngine().executeNamedQuery("PRODUCTCATEGORY_DELETE_BY_CATEGORY_UID", new Long(categoryUid));
	}

	/**
	 * Deletes the category and subcategories.
	 * Fails with exception if any category is not empty.
	 *
	 * @param category the category to remove
	 * @throws EpServiceException - in case of any errors
	 */
	private void removeCategory(final Category category) throws EpServiceException {
		sanityCheck();
		if (hasProduct(category.getUidPk())) {
			throw new EpCategoryNotEmptyException("Category must be empty.");
		}
		// Delete the sub-categories
		for (final Category subCategory : category.getChildren()) {
			removeCategory(subCategory);
		}

		getPersistenceEngine().delete(category);
		addCategoryDeleted(category.getUidPk());
	}

	private void addCategoryDeleted(final long uid) {
		final CategoryDeleted categoryDeleted = getBean(ContextIdNames.CATEGORY_DELETED);
		categoryDeleted.setCategoryUid(uid);
		categoryDeleted.setDeletedDate(new Date());
		getPersistenceEngine().save(categoryDeleted);
	}

	@Override
	public void removeCategoryTree(final long categoryUid) throws EpServiceException {
		sanityCheck();

		final Category curCategory = load(categoryUid, categoryLoadTunerDefault);

		if (curCategory.getParent() != null) {
			getPersistenceEngine().evictObjectFromCache(curCategory.getParent());
		}
		getPersistenceEngine().evictObjectFromCache(curCategory);

		// First, remove any categories that are linked to this one
		final List<Category> linkedCategories = findLinkedCategories(categoryUid);
		for (final Category linkedCategory : linkedCategories) {
			removeLinkedCategoryTreeInternal(linkedCategory);
		}

		// Second, remove the category
		removeCategory(curCategory);

	}

	/**
	 * Re orders (swaps the ordering field) of the two parameter categories. If ordering hasn't been set before, then will go thru the whole parent
	 * category and order all the child categories first.
	 *
	 * @param uidOne UID of a category to reorder
	 * @param uidTwo UID of a category to reorder
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public void updateOrder(final long uidOne, final long uidTwo) throws EpServiceException {
		// don't need to populate attributes
		fetchPlanHelper.configureCategoryFetchPlan(categoryLoadTunerMinimal);
		Category one = getPersistenceEngine().load(getAbstractCategoryImplClass(), uidOne);
		Category two = getPersistenceEngine().load(getAbstractCategoryImplClass(), uidTwo);
		fetchPlanHelper.clearFetchPlan();
		if (one.getCatalog().getUidPk() != two.getCatalog().getUidPk()) {
			throw new EpServiceException("Cannot update the order of categories in different catalogs.");
		}

		if (one.getOrdering() == two.getOrdering()) {
			final Category parent = one.getParent();
			List<Category> children;
			if (parent == null) { // root category
				children = listRootCategories(one.getCatalog(), false);
			} else {
				children = new ArrayList<Category>(parent.getChildren());
			}
			Collections.sort(children);

			int ordering = 0; // set all orderings in the whole category
			for (Category category : children) {
				category.setOrdering(ordering++);

				final Category result = saveOrUpdate(category);

				// refresh
				if (result.getUidPk() == uidOne) {
					one = result;
				}
				if (result.getUidPk() == uidTwo) {
					two = result;
				}
			}
		}

		final int tempOrdering = one.getOrdering(); // swap orderings
		one.setOrdering(two.getOrdering());
		two.setOrdering(tempOrdering);
		saveOrUpdate(one);
		saveOrUpdate(two);
	}

	/**
	 * Updates the position (parent category) and ordering of category after a drag and drop.
	 *
	 * @param uidPk UID of category
	 * @param oldParentUid UID of old parent
	 * @param newParentUid UID of new parent
	 * @param newPreviousCategoryUid UID of category directly in front of category
	 *            (ordering-wise).
	 */
	@Override
	public void updatePosition(final long uidPk, final long oldParentUid, final long newParentUid,
			final Long newPreviousCategoryUid) {
		final Category category = getPersistenceEngine().load(getAbstractCategoryImplClass(), uidPk);
		final List<Category> tmpsiblings = getSubCategories(newParentUid);

		final List<Category> siblings = new ArrayList<Category>(tmpsiblings.size());
		siblings.addAll(tmpsiblings);

		if (oldParentUid == newParentUid) { // remove from sibling list
			for (final Iterator<Category> siblingIter = siblings.iterator(); siblingIter.hasNext();) {
				if (siblingIter.next().getUidPk() == uidPk) {
					siblingIter.remove();
					break;
				}
			}
		} else { // set new parent category
			final long rootCategory = 0;
			if (newParentUid == rootCategory) {
				category.setParent(null);
			} else {
				category.setParent(getPersistenceEngine().load(getAbstractCategoryImplClass(), newParentUid));
			}
		}

		// iterate thru siblings once and set order
		if (newPreviousCategoryUid == null) { // we are the 1st node
			siblings.add(0, category);
		} else {
			// iterate thru all siblings and add new category in correct place
			for (int i = 0; i < siblings.size(); i++) {
				final Category sibling = siblings.get(i);
				if (sibling.getUidPk() == newPreviousCategoryUid.longValue()) {
					siblings.add(i + 1, category); // add after
					break;
				}
			}
		}

		// iterate thru all siblings, set order and update
		for (int i = 0; i < siblings.size(); i++) {
			final Category sibling = siblings.get(i);
			sibling.setOrdering(i);
			saveOrUpdate(sibling);
		}
	}

	/**
	 * Sets the <code>CategoryLoadTuner</code> for populating all data.
	 *
	 * @param categoryLoadTunerAll the <code>CategoryLoadTuner</code> for populating all data.
	 */
	public void setCategoryLoadTunerAll(final CategoryLoadTuner categoryLoadTunerAll) {
		this.categoryLoadTunerAll = categoryLoadTunerAll;
	}

	/**
	 * Sets the <code>CategoryLoadTuner</code> for populating all data including all children.
	 *
	 * @param categoryLoadTunerAllDeep the <code>CategoryLoadTuner</code> for populating all data.
	 */
	public void setCategoryLoadTunerAllDeep(final CategoryLoadTuner categoryLoadTunerAllDeep) {
		this.categoryLoadTunerAllDeep = categoryLoadTunerAllDeep;
	}

	/**
	 * Sets the <code>CategoryLoadTuner</code> for populating minimal data.
	 *
	 * @param categoryLoadTunerMinimal the <code>CategoryLoadTuner</code> for populating minimal data.
	 */
	public void setCategoryLoadTunerMinimal(final CategoryLoadTuner categoryLoadTunerMinimal) {
		this.categoryLoadTunerMinimal = categoryLoadTunerMinimal;
	}

	/**
	 * Sets the default <code>CategoryLoadTuner</code>.
	 *
	 * @param categoryLoadTunerDefault the default <code>CategoryLoadTuner</code>
	 */
	public void setCategoryLoadTunerDefault(final CategoryLoadTuner categoryLoadTunerDefault) {
		this.categoryLoadTunerDefault = categoryLoadTunerDefault;
	}

	/**
	 * Returns the <code>CategoryLoadTuner</code> for populating all data.
	 *
	 * @return the <code>CategoryLoadTuner</code> for populating all data
	 */
	public CategoryLoadTuner getCategoryLoadTunerAll() {
		return categoryLoadTunerAll;
	}

	/**
	 * Returns the default <code>CategoryLoadTuner</code>.
	 *
	 * @return the default <code>CategoryLoadTuner</code>
	 */
	public CategoryLoadTuner getCategoryLoadTunerDefault() {
		return categoryLoadTunerDefault;
	}

	/**
	 * Returns the <code>CategoryLoadTuner</code> for populating minimal data.
	 *
	 * @return the <code>CategoryLoadTuner</code> for populating minimal data
	 */
	public CategoryLoadTuner getCategoryLoadTunerMinimal() {
		return categoryLoadTunerMinimal;
	}

	/**
	 * Returns all available category UIDs as a list.
	 *
	 * @return all available category UIDs as a list
	 */
	@Override
	public List<Long> findAvailableUids() {
		sanityCheck();
		final Date now = new Date();

		// Query both the category and linked category tables
		final List<Long> availableCategoryUids = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UIDS_AVAILABLE", now, now);
		final List<Long> availableLinkedCategoryUids = getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_UIDS_AVAILABLE", now, now);

		final List<Long> result = new ArrayList<Long>(availableCategoryUids.size() + availableLinkedCategoryUids.size());
		result.addAll(availableCategoryUids);
		result.addAll(availableLinkedCategoryUids);

		return result;
	}

	/**
	 * Get the category UID of the given category guid.
	 * It is first assumed that the given category identifier is a GUID, and the method will
	 * attempt to load the Category given that assumption. If that fails, the identifier
	 * is assumed to be a UID and is returned as such.
	 *
	 * @param categoryId the category GUID or UID
	 * @param catalog the catalog to search in for the GUID
	 * @return the category UID, otherwise 0
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public long findUidById(final String categoryId, final Catalog catalog) throws EpServiceException {
		if (catalog == null) {
			throw new EpServiceException("catalog cannot be null");
		}

		final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UID_SELECT_BY_GUID", categoryId, catalog.getUidPk());

		if (results.isEmpty()) {
			try {
				return Long.valueOf(categoryId);
			} catch (final NumberFormatException e) {
				return 0L;
			}
		} else if (results.size() == 1) {
			return results.get(0);
		} else {
			throw new EpServiceException(DUPLICATE_GUID + categoryId);
		}
	}

	/**
	 * Gets the category code associated with a given category uidPk.
	 *
	 * @param uidPk - The unique ID of the category to get the code for.
	 * @return The category code of the category if it exists,
	 *         empty string otherwise.
	 */
	@Override
	public String findCodeByUid(final long uidPk) {

		List<String> result = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_CODE_SELECT_BY_UID", uidPk);

		if (result.isEmpty()) {
			// the category could be linked, which shares a code with it's master - get the code from the master category
			result = getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_CODE_SELECT_BY_UID", uidPk);
		}

		// Default the code to an empty string if not one and only one code is found
		String code = "";
		if (result.size() == 1) {
			code = result.get(0);
		}

		return code;
	}

	/**
	 * Gets the category uidPk associated with a given compound category guid.
	 *
	 * @param compoundCategoryGuid - The unique compound category guid to get the uidPk for.
	 * @return The category uidPk of the category if it exists,
	 *         -1 value otherwise.
	 */
	@Override
	public Long findUidByCompoundGuid(final String compoundCategoryGuid) {
		final Category findByCompoundGuid = findByCompoundGuid(compoundCategoryGuid);
		if (findByCompoundGuid != null) {
			return findByCompoundGuid.getUidPk();
		}

		return -1L;
	}

	/**
	 * Checks whether the given category GUID exists or not, for category, i.e. category code. A
	 * GUID exists if it is already in use in the system for that type of object.
	 *
	 * @param guid the category code
	 * @return whether the given GUID(code) exists
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public boolean guidExists(final String guid) throws EpServiceException {
		if (guid == null) {
			return false;
		}
		final List<Long> categoryCount = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_COUNT_BY_CODE", guid);
		if (categoryCount != null && categoryCount.size() == 1 && categoryCount.get(0) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns all category UIDs as a list.
	 *
	 * @return all category UIDs as a list
	 */
	@Override
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UIDS_ALL");
	}

	/**
	 * Retrieves list of <code>Category</code> UIDs where the last modified date is later than
	 * the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Category</code> whose last modified date is later than the specified date
	 */
	@Override
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	/**
	 * Retrieves list of category UIDs where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of category UIDs whose deleted date is later than the specified date
	 */
	@Override
	public List<Long> findUidsByDeletedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UIDS_SELECT_BY_DELETED_DATE", date);
	}

	/**
	 * Returns the set of category UIDs, which are ancestors of the given product UID. The category UID of the category where the product lives is
	 * not apart of the returned set.
	 *
	 * @param productUid the product UID
	 * @return a set of category UIDs
	 */
	@Override
	public Set<Long> findAncestorCategoryUidsByProductUid(final long productUid) {
		sanityCheck();
		final List<Long> categoryUidList = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UID_SELECT_BY_PRODUCT_UID", new Long(productUid));
		if (categoryUidList.isEmpty()) {
			return Collections.emptySet();
		}

		// remove duplicate UIDs
		return new HashSet<Long>(findAncestorCategoryUids(categoryUidList));
	}

	/**
	 * Returns the set of category uids which are ancestors of the given categoryUid.
	 * The given categoryUid is not a part of the returned set.
	 *
	 * @param categoryUid - The uid of the category to search for ancestors' uids on.
	 * @return A set of ancestor category UIDs.
	 */
	@Override
	public Set<Long> findAncestorCategoryUidsByCategoryUid(final long categoryUid) {
		sanityCheck();

		// remove duplicate UIDs
		return new HashSet<Long>(findAncestorCategoryUids(categoryUid));
	}

	/**
	 * Returns the set of category codes which are ancestors of the given categoryUid.
	 * The category code corresponding to the given categoryUid is not in the returned set.
	 *
	 * @param categoryUid - The uid of the category to search for ancestors' codes on.
	 * @return A set of ancestor category codes.
	 */
	@Override
	public Set<String> findAncestorCategoryCodesByCategoryUid(final long categoryUid) {
		sanityCheck();

		// Get the list of ancestor uids
		final List<Long> parentUids =  findAncestorCategoryUids(categoryUid);

		// Get the category codes associated with the ancestor uids
		final List<String> parentCodes = getPersistenceEngine()
			.retrieveByNamedQueryWithList("CATEGORY_CODES_SELECT_BY_UIDS", PLACE_HOLDER_FOR_LIST, parentUids);

		return new HashSet<String>(parentCodes);
	}

	/**
	 * Returns the list of category uids which are ancestors of the given categoryUid.
	 * The given categoryUid is not a part of the returned list. The categoryUid is
	 * turned into a list for further processing.
	 *
	 * @param categoryUid - The uid of the category to search for ancestors' codes on.
	 * @return A list of ancestor category UIDs.
	 */
	private List<Long> findAncestorCategoryUids(final long categoryUid) {
		// Turn the categoryUid into a list to be passed to the query
		final List<Long> categoryUids = Arrays.asList(categoryUid);

		return findAncestorCategoryUids(categoryUids);
	}

	/**
	 * Returns the list of category uids which are ancestors of the given categoryUids.
	 * The given categoryUids are not a part of the returned list.
	 *
	 * @param categoryUids - The uids of the categories to search for ancestors' codes on.
	 * @return A list of ancestor category UIDs.
	 */
	private List<Long> findAncestorCategoryUids(final List<Long> categoryUids) {
		final List<Long> result = new ArrayList<Long>();

		// Get the list of immediate parent uids
		List<Long> parentUids = getPersistenceEngine()
			.retrieveByNamedQueryWithList("CATEGORY_UID_SELECT_BY_CHILDREN_UIDS", PLACE_HOLDER_FOR_LIST, categoryUids);

		// For each level of parents, get their parents' uids and add them to the list until no parents are left
		while (!parentUids.isEmpty()) {
			result.addAll(parentUids);
			parentUids = getPersistenceEngine()
				.retrieveByNamedQueryWithList("CATEGORY_UID_SELECT_BY_CHILDREN_UIDS", PLACE_HOLDER_FOR_LIST, parentUids);
		}

		return result;
	}

	/**
	 * Returns a list of <code>Category</code> based on the given UIDs. The returned category will be populated based on the default load tuner.
	 *
	 * @param categoryUids a collection of category UIDs
	 * @return a list of <code>Category</code>s
	 */
	@Override
	public List<Category> findByUids(final Collection<Long> categoryUids) {
		return findByUids(categoryUids, categoryLoadTunerDefault);
	}

	/**
	 * Returns a list of <code>Category</code> based on the given UIDs. The returned category
	 * will be populated based on the given load tuner.
	 *
	 * @param categoryUids a collection of category UIDs
	 * @param loadTuner the load tuner
	 * @return a list of <code>Category</code>s
	 */
	@Override
	public List<Category> findByUids(final Collection<Long> categoryUids, final CategoryLoadTuner loadTuner) {
		sanityCheck();

		if (categoryUids == null || categoryUids.isEmpty()) {
			return Collections.emptyList();
		}

		fetchPlanHelper.configureCategoryFetchPlan(loadTuner);
		final List<Category> result = getPersistenceEngine().retrieveByNamedQueryWithList("CATEGORY_BY_UIDS", PLACE_HOLDER_FOR_LIST, categoryUids);
		fetchPlanHelper.clearFetchPlan();
		return result;
	}


	/**
	 * Returns a list of <code>Category</code> based on the given UIDs using a {@link FetchGroupLoadTuner}. The returned category
	 * will be populated based on the given load tuner.
	 *
	 * @param categoryUids a collection of category UIDs
	 * @param loadTuner the load tuner
	 * @return a list of <code>Category</code>s
	 */
	@Override
	public List<Category> findByUidsWithFetchGroupLoadTuner(final Collection<Long> categoryUids, final FetchGroupLoadTuner loadTuner) {
		sanityCheck();

		if (categoryUids == null || categoryUids.isEmpty()) {
			return Collections.emptyList();
		}

		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		final List<Category> result = getPersistenceEngine().retrieveByNamedQueryWithList("CATEGORY_BY_UIDS", PLACE_HOLDER_FOR_LIST, categoryUids);
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	/**
	 * Returns a list of <code>Category</code> UIDs based on the given Catalog UIDPK.
	 *
	 * @param catalogUid identifier of the catalog
	 * @return a list of <code>Catalog</code>s UIDs
	 */
	@Override
	public List<Long> findCategoryUidsForCatalog(final long catalogUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UIDS_FOR_CATALOG", catalogUid);
	}

	/**
	 * Returns a list of <code>Category</code> UIDs based on the given Catalog Code.
	 *
	 * @param catalogCode is code of the catalog
	 * @return a list of <code>Catalog</code>s UIDs
	 */
	@Override
	public List<Long> findCategoryUidsForCatalog(final String catalogCode) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_UIDS_FOR_CATALOG_BY_CODE", catalogCode);
	}

	/**
	 * Get a list of featured product by the category UID.
	 *
	 * @param categoryUid the category uidPk.
	 * @return a list of Object arrays where the element at index <code>0</code> is a
	 *         <code>Product</code> and the element at index <code>1</code> is an
	 *         <code>Integer</code> feature order value.
	 */
	@Override
	public List<Object[]> getFeaturedProductsList(final long categoryUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FEATURED_PRODUCT_SELECT_BY_CAT_UID", new Long(categoryUid));
	}

	/**
	 * Get a list of featured product UID by the category UID.
	 *
	 * @param categoryUid the category UID.
	 * @return a list of product UID.
	 */
	@Override
	public List<Long> findFeaturedProductUidList(final long categoryUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FEATURED_PRODUCT_UID_SELECT_BY_CAT_UID", new Long(categoryUid));
	}

	/**
	 * Set the <code>ProductService</code>.
	 *
	 * @param productService the <code>ProductService</code>
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Get a reference to the product service. Note that the product service may not be set through Spring due to a circular reference.
	 *
	 * @return the <code>ProductService</code> instance
	 */
	private ProductService getProductService() {
		if (productService == null) {
			productService = getBean(ContextIdNames.PRODUCT_SERVICE);
		}
		return productService;
	}

	/**
	 * Sets the fetch plan helper.
	 *
	 * @param fetchPlanHelper the fetch plan helper
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Creates a new linked category (in the given <code>catalog</code>) to the
	 * given <code>masterCategory</code>and additional linked categories for
	 * all of the <code>masterCategory</code>'s sub-categories. The top-level
	 * linked category is set to the given <code>parentCategory</code>.
	 * This implementation calls {@link #addLinkedCategory(Category, Category, Catalog, int)}.
	 *
	 * @param masterCategory the category to which the top-level linked category is linked.
	 * @param parentCategory the category to be the parent of the new top-level linked category
	 * @param catalog the catalog in which the new linked categories should be created
	 * @return the new top-level linked category
	 */
	protected Category addLinkedCategory(final Category masterCategory, final Category parentCategory, final Catalog catalog) {
		return addLinkedCategory(masterCategory, parentCategory, catalog, 0);
	}

	/**
	 * Creates a {@link Category} that is linked to the given {@code masterCategory}, with its
	 * parent set to the given {@code parentCategory}, and its catalog set to the given
	 * {@link Catalog}. This method creates a linked category to the given {@code masterCategory},
	 * derives all of its products, and then recursively does the same for any of the
	 * {@code masterCategory}'s sub-categories.
	 * This implementation calls {@link #updateProductsWithNewLinkedCategory(Category)}
	 * and {@link #saveOrMerge(Category)}.
	 *
	 * @param masterCategory the {@link Category} to link to
	 * @param parentCategory the {@link Category} to set the parent to; {@code null} if linked
	 *            category is a root category
	 * @param catalog the {@link Catalog} that contains the linked category
	 * @param depth the current depth of the tree of linked categories
	 * @return the newly created linked {@link Category}
	 * @throws DuplicateKeyException if a linked category for the given master category already exists in the given catalog
	 * @throws IllegalOperationException if an attempt is made to add a linked category to a master catalog.
	 */
	protected Category addLinkedCategory(final Category masterCategory, final Category parentCategory, final Catalog catalog, final int depth) {
		if (catalog.isMaster()) {
			throw new IllegalOperationException("A linked category cannot be created in a master catalog.");
		}
		if (linkedCategoryExists(masterCategory.getCode(), catalog.getCode())) {
			throw new DuplicateKeyException("A category linked to " + masterCategory.getCode() + " already exists in catalog " + catalog.getCode());
		}

		final Category newLinkedCategory = createLinkedCategory(masterCategory, parentCategory, catalog);

		for (final Category currCategory : masterCategory.getChildren()) {
			addLinkedCategory(currCategory, newLinkedCategory, catalog, depth + 1);
		}

		if (depth == 0) { //We're at the top of the tree of new linked categories
			final Category updatedCategory = saveOrMerge(newLinkedCategory);
			updateCategoryProductsRecursively(updatedCategory);
			return updatedCategory;
		}

		// Products start to get indexed as soon as they are saved resulting in invalid categories
		// for products, need to re-index those products now
		return newLinkedCategory;
	}

	/**
	 * Traverses recursively through all the categories and calls {@link #updateProductsWithNewLinkedCategory(Category)}.
	 *
	 * @param category the category which products have to be updated
	 */
	private void updateCategoryProductsRecursively(final Category category) {
		updateProductsWithNewLinkedCategory(category);
		//loop through all the new linked categories, and for each one loop through its products, adding the category to each product
		for (final Category subCategory : category.getChildren()) {
			updateCategoryProductsRecursively(subCategory);
		}
	}

	/**
	 * Calls the persistence engine to save or merge the given category.
	 * This implementation does not call the product service to update
	 * the category's products.
	 * @param category the category to save
	 * @return the saved category
	 */
	protected Category saveOrMerge(final Category category) {
		final Category updatedCategory = getPersistenceEngine().saveOrMerge(category);
		getIndexNotificationService().addNotificationForEntityIndexUpdate(IndexType.CATEGORY, updatedCategory.getUidPk());
		return updatedCategory;
	}

	/**
	 * Creates a new {@link com.elasticpath.domain.catalog.impl.LinkedCategoryImpl}. The ordering will be the same as the given Master Category,
	 * and the 'included' bit will be set to true.
	 * @param masterCategory the linked category's master category
	 * @param parentCategory the linked category's parent category
	 * @param catalog the catalog in which the linked category will be created
	 * @return the new linked category
	 */
	private Category createLinkedCategory(final Category masterCategory, final Category parentCategory, final Catalog catalog) {
		final Category newLinkedCategory = getBean(ContextIdNames.LINKED_CATEGORY);
		newLinkedCategory.setCatalog(catalog);
		newLinkedCategory.setMasterCategory(masterCategory);
		newLinkedCategory.setParent(parentCategory);
		newLinkedCategory.setIncluded(true);
		newLinkedCategory.setOrdering(masterCategory.getOrdering());
		return newLinkedCategory;
	}

	/**
	 * Adds the given linked category to every product in the given linked category's
	 * {@code masterCategory}.
	 *
	 * Makes the given linked category the default category in this catalog for all affected products.
	 *
	 * @param subCategory the linked subCategory that should be added to products
	 */
	protected void updateProductsWithNewLinkedCategory(final Category subCategory) {
		// JPA seems to trigger a field access when a category is included.
		fetchPlanHelper.configureFetchGroupLoadTuner(getLinkProductCategoryLoadTuner(), true);
		for (final Product currProduct
				: getProductService().findByCategoryUid(subCategory.getMasterCategory().getUidPk(), getLinkProductCategoryLoadTuner())) {
			currProduct.addCategory(subCategory);
			currProduct.setCategoryAsDefault(subCategory);
			getProductService().saveOrUpdate(currProduct);
		}
		getProductService().notifyCategoryUpdated(subCategory);
		fetchPlanHelper.clearFetchPlan();
	}

	/**
	 * Creates a {@link Category} that is linked to the given {@code masterCategory}, with its
	 * parent set to the given {@code parentCategory}, and its catalog set to the given
	 * {@link Catalog}. This method creates a linked category to the given {@code masterCategory},
	 * and then recursively does the same for any of the {@code masterCategory}'s sub-categories.
	 *
	 * For each product in each of the master categories, the new linked category of the same level
	 * is added to the product. Once a linked category has been added to all of the appropriate products,
	 * the product service is notified that the category has been updated.
	 *
	 * This implementation uses the given UIDs to load the master category, parent category, and catalog from the
	 * database and calls {@link #addLinkedCategory(Category, Category, Catalog)} to do the work.
	 *
	 * @param masterCategoryUid the uid of category to link to
	 * @param parentCategoryUid the uid of category to set the parent, set to -1 if linked
	 *            category is a root category
	 * @param catalogUid the catalog uid that contains the linked category
	 * @return the newly created linked {@link Category}
	 */
	@Override
	public Category addLinkedCategory(final long masterCategoryUid, final long parentCategoryUid, final long catalogUid) {
		final FetchGroupLoadTuner uidLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		uidLoadTuner.addFetchGroup(FetchGroupConstants.NONE);

		// needed for linking children categories
		final FetchGroupLoadTuner categoryLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		categoryLoadTuner.addFetchGroup(FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH);
		final Category masterCategory = load(masterCategoryUid, categoryLoadTuner);

		Category parentCategory = null;
		if (parentCategoryUid != -1) {
			parentCategory = load(parentCategoryUid, categoryLoadTuner);
		}

		final Catalog catalog = catalogService.load(catalogUid, uidLoadTuner, true);
		return addLinkedCategory(masterCategory, parentCategory, catalog);
	}

	/**
	 * Determines the number of root categories in the given <code>Catalog</code>.
	 *
	 * @param catalogUid the id of the <code>Catalog</code> to get the count for
	 * @return the number of root categories
	 */
	@Override
	public int getRootCategoryCount(final long catalogUid) {
		Long numRootCategoriesInCatalog = 0L;
		final List<Object> rootCountResults =
			getPersistenceEngine().retrieveByNamedQuery("COUNT_ROOT_CATEGORIES", catalogUid);
		if (rootCountResults.size() == 1) {
			numRootCategoriesInCatalog = (Long) rootCountResults.get(0);
		}
		return numRootCategoriesInCatalog.intValue();
	}

	/**
	 * Determines the number of sub categories in the given <code>Category</code>.
	 *
	 * @param categoryUid the id of the <code>Category</code> to get the count for
	 * @return the number of root categories
	 */
	@Override
	public int getSubCategoryCount(final long categoryUid) {
		int numSubCategories = 0;
		final List<Object> rootCountResults =
			getPersistenceEngine().retrieveByNamedQuery("COUNT_SUBCATEGORIES_IN_CATEGORY", categoryUid);
		if (rootCountResults.size() == 1) {
			numSubCategories = ((Long) rootCountResults.get(0)).intValue();
		}
		return numSubCategories;
	}

	/**
	 * Adds all products in the master category of the given {@link Category linked category}
	 * (including sub-categories). A linked category is one that {@link Category#isLinked()}
	 * returns {@code true}.
	 *
	 * @param linkedCategory the {@link Category} whose products/sub-category should be included
	 * @return the updated category
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Category addLinkedCategoryProducts(final Category linkedCategory) {
		if (!linkedCategory.isLinked()) {
			throw new EpServiceException("linkedCategory must be a linked category");
		}

		final Collection<Product> products = getProductService().findByCategoryUid(linkedCategory.getMasterCategory().getUidPk(),
				getLinkProductCategoryLoadTuner());

		// JPA seems to trigger a field access when a category is added. Use same fetch
		// plan so that fields aren't loaded because it was cleared in the previous call.
		fetchPlanHelper.configureFetchGroupLoadTuner(getLinkProductCategoryLoadTuner(), true);

		for (final Product product : products) {
			product.addCategory(linkedCategory);
			getProductDao().saveOrUpdate(product);
		}

		// Set the linked category's Include flag to true
		linkedCategory.setIncluded(true);
		final Category result = saveOrUpdate(linkedCategory);

		fetchPlanHelper.clearFetchPlan();

		// Use recursion to include products in any sub-categories
		for (final Category currCategory : result.getChildren()) {
			addLinkedCategoryProducts(currCategory);
		}

		// Now we're done we notify the indexer that the products in this category need to be reindexed.
		getProductService().notifyCategoryUpdated(result);
		return result;
	}

	/**
	 * Removes all products in the given {@link Category} (including sub-categories). The
	 * behaviour is undefined in the case where you remove products from a category for which any
	 * of the products only exists in that category.
	 *
	 * @param category the {@link Category} whose products/sub-category should be excluded
	 * @return the updated category
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Category removeCategoryProducts(final Category category) {

		Category result = category;
		getProductService().notifyCategoryUpdated(result);

		removeProductCategory(category.getUidPk());

		// Set the linked category's Include flag to false
		if (category.isLinked()) {
			category.setIncluded(false);
			result = saveOrUpdate(category);
		}

		// Use recursion to exclude products in any sub-categories
		for (final Category currCategory : result.getChildren()) {
			removeCategoryProducts(currCategory);
		}

		// Products start to get indexed as soon as they are saved resulting in invalid categories
		// for products, need to re-index those products now
		return result;
	}

	/**
	 * Returns a <code>List</code> of Category objects linked to the Category indicated by the given <code>masterCategoryUid</code>.
	 *
	 * @param masterCategoryUid the master category uid to look up
	 * @return a <code>List</code> of all UIDs of all Category objects linked to the Category indicated by the given <code>masterCategoryUid</code>
	 */
	@Override
	public List<Category> findLinkedCategories(final long masterCategoryUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_SELECT_BY_MASTER_CATEGORY_UID", new Long(masterCategoryUid));
	}

	@Override
	public void removeLinkedCategoryTree(final Category linkedCategory) throws EpServiceException {
		if (!linkedCategory.isLinked()) {
			throw new IllegalArgumentException("The given category(uidPk=" + linkedCategory.getUidPk() + ") is not a linked category.");
		}
		getPersistenceEngine().evictObjectFromCache(linkedCategory);

		// Remove the linked categories themselves, load a new category just in case all
		// children have not been populated yet
		final FetchGroupLoadTuner categoryLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		categoryLoadTuner.addFetchGroup(FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH);
		removeLinkedCategoryTreeInternal(load(linkedCategory.getUidPk(), categoryLoadTuner));
	}

	private void removeLinkedCategoryTreeInternal(final Category linkedCategory) {
		if (!linkedCategory.isLinked()) {
			throw new IllegalArgumentException("Category(uidPk=" + linkedCategory.getUidPk() + ") is not a linked category.");
		}
		// First, exclude all the products in this category
		removeCategoryProducts(linkedCategory);

		final Set<Category> children = linkedCategory.getChildren();
		if (children != null) {
			for (final Category currCategory : children) {
				removeLinkedCategoryTreeInternal(currCategory);
			}
		}

		// delete it from the database
		getPersistenceEngine().delete(linkedCategory);
		addCategoryDeleted(linkedCategory.getUidPk());
	}

	/**
	 * Reorders the given Category up. That is, the Category's order value will be swapped with the order value of the Category above it. If there
	 * are no Category objects above this one (i.e. this Category is 'first' in the list), then do nothing.
	 *
	 * @param category the Category to reorder
	 */
	@Override
	public void updateCategoryOrderUp(final Category category) {
		final List<Category> listRootCategories;
		long categoryUidToSwap = -1;

		if (category.getParent() == null) {
			// If reordering a root category, scan the other root categories in the catalog
			listRootCategories = this.listRootCategories(category.getCatalog(), false);
		} else {
			// If reordering a non-root category, scan the other categories with the same parent
			listRootCategories = findDirectDescendantCategories(category.getParent().getUidPk());
		}

		// Find the category to swap positions with. It will be the category that is 'above' this one (order-wise).
		Category lastCategory = null;
		for (final Category currCategory : listRootCategories) {
			if (currCategory.getUidPk() == category.getUidPk()) {
				if (lastCategory != null) {
					categoryUidToSwap = lastCategory.getUidPk();
				}
				break;
			}
			lastCategory = currCategory;
		}

		if (categoryUidToSwap != -1) {
			updateOrder(category.getUidPk(), categoryUidToSwap);
		}
	}

	/**
	 * Reorders the given Category down. That is, the Category's order value will be swapped with the order value of the Category below it. If there
	 * are no Category objects below this one (i.e. this Category is 'last' in the list), then do nothing.
	 *
	 * @param category the Category to reorder
	 */
	@Override
	public void updateCategoryOrderDown(final Category category) {
		final List<Category> listRootCategories;
		long categoryUidToSwap = -1;

		if (category.getParent() == null) {
			// If reordering a root category, scan the other root categories in the catalog
			listRootCategories = this.listRootCategories(category.getCatalog(), false);
		} else {
			// If reordering a non-root category, scan the other categories with the same parent
			listRootCategories = findDirectDescendantCategories(category.getParent().getUidPk());
		}

		// Find the category to swap positions with. It will be the category that is 'below' this one (order-wise).
		for (final Iterator<Category> categoryIterator = listRootCategories.iterator(); categoryIterator.hasNext();) {
			if (categoryIterator.next().getUidPk() == category.getUidPk()) {
				if (categoryIterator.hasNext()) {
					categoryUidToSwap = categoryIterator.next().getUidPk();
				}
				break;
			}
		}

		if (categoryUidToSwap != -1) {
			updateOrder(category.getUidPk(), categoryUidToSwap);
		}
	}

	/**
	 * Populate children category to the given category. This method is mainly developed to load children
	 * categories using different fetch group from parent category.
	 * @param parentCategory the category to be populated
	 * @param recursionDepth the recursion depth
	 * @param loadTuner the load tuner
	 * @return the category with children
	 */
	@Override
	public Category populateChildenCategory(final Category parentCategory, final int recursionDepth, final FetchGroupLoadTuner loadTuner) {

		if (loadTuner != null) { //only config the load tuner for the first call, not in the recursion.
			fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		}

		Category loadedCategory = parentCategory;
		if (recursionDepth > 0) {
			loadedCategory = getPersistenceEngine().get(parentCategory.getClass(), parentCategory.getUidPk());
			recursivelyLoadChildren(loadedCategory, recursionDepth);
		}

		if (loadTuner != null) {
			fetchPlanHelper.clearFetchPlan();
		}
		return loadedCategory;
	}

	private void recursivelyLoadChildren(final Category parentCategory, final int recursionDepth) {
		if (recursionDepth > 0) {
			for (final Category child : parentCategory.getChildren()) {
				recursivelyLoadChildren(child, recursionDepth - 1);
			}
		}
	}

	private FetchGroupLoadTuner getDefaultFetchGroupLoadTuner() {
		if (defaultFetchGroupLoadTuner == null) {
			final FetchGroupLoadTuner defaultFetchGroupLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			defaultFetchGroupLoadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_BASIC,
					FetchGroupConstants.CATALOG_DEFAULTS, // need default locale
					FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH // needed for SEO
			);
			this.defaultFetchGroupLoadTuner = defaultFetchGroupLoadTuner;
		}
		return defaultFetchGroupLoadTuner;
	}

	private FetchGroupLoadTuner getLinkProductCategoryLoadTuner() {
		if (linkProductCategoryLoadTuner == null) {
			final FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			loadTuner.addFetchGroup(FetchGroupConstants.LINK_PRODUCT_CATEGORY, FetchGroupConstants.PRODUCT_HASH_MINIMAL,
					FetchGroupConstants.CATEGORY_HASH_MINIMAL, FetchGroupConstants.CATALOG_DEFAULTS);
			linkProductCategoryLoadTuner = loadTuner;
		}
		return linkProductCategoryLoadTuner;
	}

	/**
	 * Sets the {@link CatalogService} instance to use.
	 *
	 * @param catalogService the {@link CatalogService} instance to use
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public Category findByGuid(final String categoryCode, final String catalogCode, final CategoryLoadTuner loadTuner) {
		if (catalogCode == null) {
			throw new EpServiceException("catalogCode cannot be null");
		}

		if (loadTuner == null) {
			fetchPlanHelper.configureCategoryFetchPlan(categoryLoadTunerDefault);
		} else {
			fetchPlanHelper.configureCategoryFetchPlan(loadTuner);
		}

		List<Category> categories = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_SELECT_BY_GUID_AND_CATALOG_GUID",
				categoryCode,
				catalogCode);
        if (categories == null || categories.isEmpty()) {
			categories = getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_FIND_BY_GUID_CATALOG_GUID", categoryCode, catalogCode);
        }

		fetchPlanHelper.clearFetchPlan();

		final int size = categories.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID + categoryCode);
		} else if (size == 0) {
			return null;
		}
		return categories.get(0);
	}

	/**
	 * @return an instance of {@link IndexNotificationService}
	 */
	public IndexNotificationService getIndexNotificationService() {
		return getBean("indexNotificationService");
	}

	@Override
	public boolean categoryExistsWithCompoundGuid(final String compoundGuid) {
		final String categoryCode = compoundGuid.substring(0, compoundGuid.indexOf(Category.CATEGORY_GUID_DELIMITER));
		final String catalogCode = compoundGuid.substring(compoundGuid.indexOf(Category.CATEGORY_GUID_DELIMITER)
				+ Category.CATEGORY_GUID_DELIMITER.length());

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("categoryCode", categoryCode);
		parameters.put("catalogCode", catalogCode);
		List<Long> results = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
		if (results.isEmpty() || results.get(0) == 0) {
			results = getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
		}
		if (!results.isEmpty() && results.get(0) > 0) {
			return true;
		}
		return false;

	}

	@Override
	public Category findByCompoundGuid(final String compoundGuid, final FetchGroupLoadTuner fetchGroupLoadTuner) {
		final String categoryCode = compoundGuid.substring(0, compoundGuid.indexOf(Category.CATEGORY_GUID_DELIMITER));
		final String catalogCode = compoundGuid.substring(compoundGuid.indexOf(Category.CATEGORY_GUID_DELIMITER)
				+ Category.CATEGORY_GUID_DELIMITER.length());

		// we only need the UID, don't need to load anything else
		final FetchGroupLoadTuner catalogLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		catalogLoadTuner.addFetchGroup(FetchGroupConstants.NONE);
		fetchPlanHelper.configureFetchGroupLoadTuner(catalogLoadTuner);
		final Catalog catalog = catalogService.findByCode(catalogCode);
		fetchPlanHelper.clearFetchPlan();

		return findByGuid(categoryCode, catalog, fetchGroupLoadTuner);

	}

	@Override
	public Set<Long> findAncestorCategoryUidsWithTreeOrder(final Set<Long> categoryUidSet) {
		final Set<Long> resultSet = new LinkedHashSet<Long>();

		List<Long> parents = getPersistenceEngine().retrieveByNamedQueryWithList("CATEGORY_UID_SELECT_BY_CHILDREN_UIDS", PLACE_HOLDER_FOR_LIST,
				categoryUidSet);

		if (!parents.isEmpty()) {
			resultSet.addAll(findAncestorCategoryUidsWithTreeOrder(new HashSet<Long>(parents)));
			resultSet.addAll(parents);
		}

		return resultSet;
	}

	protected ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(final ProductDao productDao) {
		this.productDao = productDao;
	}

	@Override
	public int findMaxRootOrdering(final long catalogUid) {
		List<Integer> ordering = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_ROOT_MAX_ORDERING", catalogUid);
		if (ordering == null || ordering.isEmpty() || ordering.get(0) == null) {
			return 0;
		}
		return ordering.get(0);
	}

	@Override
	public int findMinRootOrdering(final long catalogUid) {
		List<Integer> ordering = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_ROOT_MIN_ORDERING", catalogUid);
		if (ordering == null || ordering.isEmpty() || ordering.get(0) == null) {
			return 0;
		}
		return ordering.get(0);
	}

	@Override
	public int findMaxChildOrdering(final Category category) {
		List<Integer> ordering = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_CHILD_MAX_ORDERING", category.getUidPk());
		if (ordering == null || ordering.isEmpty() || ordering.get(0) == null) {
			return 0;
		}
		return ordering.get(0);
	}

	@Override
	public int findMinChildOrdering(final Category category) {
		List<Integer> ordering = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_CHILD_MIN_ORDERING", category.getUidPk());
		if (ordering == null || ordering.isEmpty() || ordering.get(0) == null) {
			return 0;
		}
		return ordering.get(0);
	}

}
