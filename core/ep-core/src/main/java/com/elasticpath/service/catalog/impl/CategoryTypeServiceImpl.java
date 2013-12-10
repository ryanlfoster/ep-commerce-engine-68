package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;


/**
 * Default implementation for <code>AttributeService</code>.
 */
public class CategoryTypeServiceImpl extends AbstractEpPersistenceServiceImpl implements CategoryTypeService {

	private FetchPlanHelper fetchPlanHelper;

	private CategoryTypeLoadTuner categoryTypeLoadTunerAll;

	/**
	 * Adds the given attribute.
	 *
	 * @param categoryType the attribute to add
	 * @return the persisted instance of CategoryType
	 * @throws DuplicateKeyException - if a categoryType with the speicifed key already exists.
	 */
	public CategoryType add(final CategoryType categoryType) throws DuplicateKeyException {
		sanityCheck();
		throwExceptionIfDuplicate(categoryType);
		getPersistenceEngine().save(categoryType);
		return categoryType;
	}

	/**
	 * Updates the given CategoryType.  Will also remove attribute values for attributes which
	 * were removed.
	 *
	 * @param categoryType the CategoryType to update
	 * @return the updated category type instance
	 * @throws DuplicateKeyException - if a categoryType with the speicifed key already exists.
	 */
	public CategoryType update(final CategoryType categoryType) throws DuplicateKeyException {
		sanityCheck();

		// make copy of original
		CategoryType original = get(categoryType.getUidPk());
		original = initialize(original);
		Set<AttributeGroupAttribute> beforeSet = new HashSet<AttributeGroupAttribute>(
			original.getAttributeGroup().getAttributeGroupAttributes());
		throwExceptionIfDuplicate(categoryType);
		final CategoryType updatedCategoryType = getPersistenceEngine().merge(categoryType);

		// lookup removed attributes
		Set<Attribute> removedAttributes = categoryType.getAttributeGroup().getRemovedAttributes(beforeSet);
		if (removedAttributes == null || removedAttributes.isEmpty()) {
			return updatedCategoryType;
		}

		// removed values for removed attributes for all categories in type
		List<Category> categories = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_LIST_CATEGORY_TYPE",
				categoryType.getUidPk());
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
			category.getAttributeValueGroup().removeByAttributes(removedAttributes);
			getPersistenceEngine().merge(category);
		}
		return updatedCategoryType;
	}

	/**
	 * Delete the CategoryType.
	 *
	 * @param categoryType the CategoryType to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final CategoryType categoryType) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(categoryType);
	}

	/**
	 * Lists all CategoryType stored in the database.
	 *
	 * @return a list of CategoryType
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public List<CategoryType> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPE_SELECT_ALL");
	}

	/**
	 * Finds all the {@link CategoryType}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link CategoryType}s
	 * @throws EpServiceException in case of any errors
	 */
	public List<CategoryType> findAllCategoryTypeFromCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPE_SELECT_CATALOG_ALL", catalogUid);
	}

	/**
	 * Lists all CategoryType Info stored in the database. Each element in the
	 * list will be composed of a 2 element String array: [uid, name] to ease
	 * DWR conversion.
	 *
	 * @return a list of CategoryType Info
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public List<String[]> listInfo() throws EpServiceException {
		List<CategoryType> categoryTypeList = list();
		List<String[]> arrayList = new ArrayList<String[]>(categoryTypeList.size());
		for (int i = 0; i < categoryTypeList.size(); i++) {
			CategoryType categoryType = categoryTypeList.get(i);
			arrayList.add(getArray(categoryType));
		}
		return arrayList;
	}

	private String[] getArray(final CategoryType categoryType) {
		return new String[] { String.valueOf(categoryType.getUidPk()), categoryType.getName() };
	}

	/**
	 * Lists all categoryType uids used by categories.
	 *
	 * @return a list of used categoryType uids
	 */
	public List<Long> listUsedUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPE_USED_UIDS");
	}

	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	public boolean isInUse(final long uidToCheck) throws EpServiceException {
		sanityCheck();
		return !getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPES_IN_USE", uidToCheck).isEmpty();
	}

	/**
	 * Deletes the all category types belonging to the catalog specified by the given catalog uid.
	 *
	 * @param catalogUid the catalog of the category types to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void removeCategoryTypes(final long catalogUid) throws EpServiceException {
		sanityCheck();

		getPersistenceEngine()
				.executeNamedQuery("CATEGORY_TYPE_DELETE_BY_CATALOG_UID", new Long(catalogUid));
	}

	/**
	 * Initialize (fill in) category attributes for the given <code>CategoryType</code>.
	 * DWR outbound conversion will fail on lazy load errors if the attributes
	 * themselves are not loaded also.
	 * @return categoryType with attributeGroup filled in.
	 * @param categoryType categoryType that needs attributes filled in.
	 */
	public CategoryType initialize(final CategoryType categoryType) {
		sanityCheck();
		this.fetchPlanHelper.configureCategoryTypeFetchPlan(this.categoryTypeLoadTunerAll);
		CategoryType freshCategoryType = getPersistentBeanFinder().get(ContextIdNames.CATEGORY_TYPE, categoryType.getUidPk());
		this.fetchPlanHelper.clearFetchPlan();
		if (freshCategoryType == null) {
			return null;
		}
		return freshCategoryType;
	}

	/**
	 * Get the categoryType with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param uid the CategoryType UID
	 *
	 * @return the CategoryType if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public CategoryType get(final long uid) throws EpServiceException {
		sanityCheck();
		CategoryType categoryType = null;
		if (uid <= 0) {
			categoryType = getBean(ContextIdNames.CATEGORY_TYPE);
		} else {
			categoryType = getPersistentBeanFinder().get(ContextIdNames.CATEGORY_TYPE, uid);
		}
		return categoryType;

	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid
	 *            the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Load method for all persistable domain models specifying fields to be loaded.
	 *
	 * @param uid the persisted instance uid
	 * @param fieldsToLoad the fields of this object that need to be loaded
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		fetchPlanHelper.addFields(getElasticPath().getBeanImplClass(ContextIdNames.CATEGORY_TYPE), fieldsToLoad);
		Object object = get(uid);
		fetchPlanHelper.clearFetchPlan();
		return object;
	}

	/**
	 * Check if the category type is existed.
	 * @param type the categoryType to be checked
	 */
	protected void throwExceptionIfDuplicate(final CategoryType type) {
		Long count = getPersistenceEngine().<Long>retrieveByNamedQuery("CATEGORY_TYPE_COUNT_BY_NAME",
				type.getName(), type.getUidPk())
				.get(0);
		if (count.longValue() != 0) {
			throw new DuplicateKeyException("CategoryType name '" + type.getName()
				+ "' already exists.");
		}
	}

	/**
	 * Finds categoryType for given name.
	 * @param name category type name.
	 * @return category type
	 */
	public CategoryType findCategoryType(final String name) {
		sanityCheck();
		if (name == null) {
			throw new EpServiceException("Cannot retrieve null name.");
		}
		List<CategoryType> typeList = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPE_FIND_BY_NAME", name);
		CategoryType categoryType = null;
		if (typeList.size() == 1) {
			categoryType = typeList.get(0);
		} else if (typeList.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate category type name: " + name);
		}
		return categoryType;
	}

	@Override
	public CategoryType findByGuid(final String guid) {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}
		List<CategoryType> typeList = getPersistenceEngine().retrieveByNamedQuery("CATEGORY_TYPE_FIND_BY_GUID", guid);
		CategoryType categoryType = null;
		if (typeList.size() == 1) {
			categoryType = typeList.get(0);
		} else if (typeList.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate category type guid: " + guid);
		}
		return categoryType;
	}

	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	public void setCategoryTypeLoadTunerAll(final CategoryTypeLoadTuner categoryTypeLoadTunerAll) {
		this.categoryTypeLoadTunerAll = categoryTypeLoadTunerAll;
	}

}
