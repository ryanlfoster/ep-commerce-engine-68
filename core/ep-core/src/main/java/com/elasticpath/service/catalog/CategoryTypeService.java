package com.elasticpath.service.catalog;

import java.util.List;

import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;


/**
 * Provide attribute related business service.
 */
public interface CategoryTypeService extends EpPersistenceService {

	/**
	 * Adds the given attribute.
	 *
	 * @param categoryType the categoryType to add
	 * @return the persisted instance of categoryType
	 * @throws DuplicateKeyException - if a categoryType with the speicifed key already exists.
	 */
	CategoryType add(final CategoryType categoryType) throws DuplicateKeyException;

	/**
	 * Updates the given categoryType.
	 *
	 * @param categoryType the categoryType to update
	 * @return the updated category type instance
	 * @throws DuplicateKeyException - if a categoryType with the speicifed key already exists.
	 */
	CategoryType update(final CategoryType categoryType) throws DuplicateKeyException;

	/**
	 * Delete the categoryType.
	 *
	 * @param categoryType the categoryType to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final CategoryType categoryType) throws EpServiceException;

	/**
	 * Lists all categoryType stored in the database.
	 *
	 * @return a list of categoryType
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<CategoryType> list() throws EpServiceException;
	
	/**
	 * Finds all the {@link CategoryType}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link CategoryType}s
	 * @throws EpServiceException in case of any errors
	 */
	List<CategoryType> findAllCategoryTypeFromCatalog(final long catalogUid) throws EpServiceException;

	/**
	 * Lists all categoryType uids used by categories.
	 *
	 * @return a list of used categoryType uids
	 */
	List<Long> listUsedUids();
	
	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	boolean isInUse(final long uidToCheck) throws EpServiceException;

	/**
	 * Deletes all category types belonging to the catalog specified by the given catalog uid.
	 * 
	 * @param catalogUid the catalog of the category types to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void removeCategoryTypes(final long catalogUid) throws EpServiceException;
	
	/**
	 * Initialize (fill in) category attributes for the given <code>CategoryType</code>.
	 * @param categoryType categoryType that needs attributes filled in.
	 * @return a categoryType with the attributeGroup filled in.
	 */
	CategoryType initialize(CategoryType categoryType);

	/**
	 * Lists all CategoryType Info stored in the database. Each element in the
	 * list will be composed of a 2 element String array: [uid, name] to ease
	 * DWR conversion.
	 *
	 * @return a list of CategoryType Info
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<String[]> listInfo() throws EpServiceException;
	
	/**
	 * Finds categoryType for given name.
	 * @param name category type name.
	 * @return category type
	 */
    CategoryType findCategoryType(final String name);

	/**
	 * Finds categoryType for given guid.
	 * @param guid the guid
	 * @return category type
	 */
    CategoryType findByGuid(final String guid);
    
}