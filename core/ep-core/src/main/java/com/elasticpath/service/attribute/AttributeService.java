package com.elasticpath.service.attribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.persistence.support.DistinctAttributeValueCriterion;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide attribute related business service.
 */
public interface AttributeService extends EpPersistenceService {

	/**
	 * Adds the given attribute.
	 *
	 * @param attribute the attribute to add
	 * @return the persisted instance of attribute
	 * @throws DuplicateKeyException - if the speicifed attribute key is already in use.
	 */
	Attribute add(final Attribute attribute) throws DuplicateKeyException;

	/**
	 * Updates the given attribute.
	 *
	 * @param attribute the attribute to update
	 * @return the updated attribute instance
	 * @throws DuplicateKeyException - if the speicifed attribute key is already in use.
	 */
	Attribute update(final Attribute attribute) throws DuplicateKeyException;

	/**
	 * Delete the attribute.
	 *
	 * @param attribute the attribute to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final Attribute attribute) throws EpServiceException;

	/**
	 * Load the attribute with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param attributeUid the attribute UID
	 *
	 * @return the attribute if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	Attribute load(final long attributeUid) throws EpServiceException;

	/**
	 * Get the attribute with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param attributeUid the attribute UID
	 *
	 * @return the attribute if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	Attribute get(final long attributeUid) throws EpServiceException;

	/**
	 * Lists all attribute stored in the database.
	 *
	 * @return a list of attribute
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<Attribute> list() throws EpServiceException;
	
	/**
	 * Retrieves a collection of {@link Attribute}s that are in the given catalog (via it's UID)
	 * or is a global attribute.
	 * 
	 * @param catalogUid the catalog UID
	 * @return a collection of {@link Attribute}s
	 * @throws EpServiceException in case of any errors
	 * @deprecated Getting all global attributes without specifying a usage type is discouraged. 
	 * 		Instead use {@link #findAllCatalogOrGlobalAttributesByType(long, Collection)}
	 */
	@Deprecated
	Collection<Attribute> findAllCatalogOrGlobalAttributes(final long catalogUid) throws EpServiceException;
	
	/**
	 * Retrieves a collection of all {@link Attribute}s that are in the given catalog (via it's UID) 
	 * and adds up all global attributes of the given usage types.
	 * 
	 * @param catalogUid the catalog UID
	 * @param globalAttributeUsage attribute usage {@link AttributeUsage} constants for retrieving global attributes
	 * @return a collection of {@link Attribute}s
	 * @throws EpServiceException in case of any errors
	 */
	Collection<Attribute> findAllCatalogAndGlobalAttributesByType(final long catalogUid, 
			final Collection<Integer> globalAttributeUsage) throws EpServiceException;

	/**
	 * Retrieves a collection of {@link Attribute}s that are global attributes.
	 * 
	 * @return a collection of {@link Attribute}s
	 * @throws EpServiceException in case of any errors
	 */
	Collection<Attribute> findAllGlobalAttributes() throws EpServiceException;

	/**
	 * Checks whether the given attribute key exists or not.
	 *
	 * @param key the attribute key.
	 * @return true if the given key exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	boolean keyExists(final String key) throws EpServiceException;

	/**
	 * Check whether the given attribute's key exists or not.
	 *
	 * @param attribute the attribute to check
	 * @return true if a different attribute with the given attribute's key exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean keyExists(final Attribute attribute) throws EpServiceException;

	/**
	 * Find the attribute with the given key.
	 *
	 * @param key the attribute key.
	 * @return the attribute that matches the given key, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Attribute findByKey(final String key) throws EpServiceException;

	/**
	 * Return the map for attribute usage value-description.
	 *
	 * @return the map for attribute usage value-description
	 */
	Map<String, String> getAttributeUsageMap();

	/**
	 * Return the map for attribute type value-description.
	 *
	 * @return the map for attribute type value-description
	 */
	Map<String, String> getAttributeTypeMap();

	/**
	 * Return a list of uids for all attributes in use.
	 *
	 * @return a list of uids for all attributes in use
	 */
	List<Long> getAttributeInUseUidList();

	/**
	 * Return a list of attributes used for categories.
	 * @return a list of category attributes.
	 */
	List<Attribute> getCategoryAttributes();

	/**
	 * Return a list of attributes used for products.
	 * @return a list of products attributes.
	 */
	List<Attribute> getProductAttributes();

	/**
	 * Return a list of attributes used for skus.
	 * @return a list of sku attributes.
	 */
	List<Attribute> getSkuAttributes();
	
	/**
	 * Return the list of attributes used for ALL But a particular AttributeUsage.
	 * 
	 * @param usage The AttributeUsage.
	 * @return the List of Attributes.
	 */
	List<Attribute> getAttributes(final AttributeUsage usage);
	
	/**
	 * For a given attribute, the set of distinct values currently
	 * existing for that attribute is returned.
	 * @param attribute the attribute for which the distinct attribute values are to be returned
	 * @param languageCode the lower case code of the language for which attributes are to be returned.
	 * @return a List of matching <code>AttributeValue</code>s
	 */
	List<String> getDistinctAttributeValueList(final Attribute attribute, final String languageCode);
	
	/**
	 * Sets the generator for criteria used to query the persistence layer for distinct lists of attribute values.
	 * @param distinctAttributeValueCriterion the <code>DistinctAttributeValueCriterion</code>
	 */
	void setDistinctAttributeValueCriterion(final DistinctAttributeValueCriterion distinctAttributeValueCriterion);
	
	/**
	 * Returns a map of all system attributes.
	 *
	 * @return a map of all system attributes
	 */
	Map<String, Attribute> getCustomerProfileAttributesMap();
	
	/**
	 * Returns a list of all system attributes.
	 *
	 * @return a list of all system attributes
	 */
	List<Attribute> getCustomerProfileAttributes();
	
	/**
	 * Returns a list of attributes exclude customer profile attribute.
	 *
	 * @return a list of attributes exclude customer profile attribute
	 */
	List<Attribute> getAttributesExcludeCustomerProfile();
	
	/**
	 * Return a list of uids for all customer profile attributes in use.
	 *
	 * @return a list of uids for all customer profile attributes in use
	 */
	List<Long> getCustomerProfileAttributeInUseUidList();
	
	/**
	 * Checks whether the given attribute name exists in this attribute usage or not.
	 *
	 * @param name the attribute name.
	 * @param attributeUsage the attribute usage.
	 * @return true if the given name exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	boolean nameExistsInAttributeUsage(final String name, final AttributeUsage attributeUsage) throws EpServiceException;

	/**
	 * Checks whether the given attribute name exists in this attribute usage or not.
	 *
	 * @param attribute the attribute to check
	 * @return true if a different attribute with the given attribute's name exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean nameExistsInAttributeUsage(final Attribute attribute) throws EpServiceException;
	
	/**
	 * Find the attribute with the given name and attribute usage.
	 *
	 * @param name the attribute name.
	 * @param attributeUsage the attribute usage.
	 * @return the attribute that matches the given name and attribute usage, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Attribute findByNameAndUsage(final String name, final AttributeUsage attributeUsage) throws EpServiceException;
	
	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	boolean isInUse(final long uidToCheck) throws EpServiceException;
	
	/**
	 * Find the attribute with the given catalog and attribute usage.
	 * 
	 * @param catalogUid the catalog uidPk
	 * @param attributeUsageId the attributeUsage id
	 * @return List of attributes by the given catalog and attribute usage id
	 */
	List<Attribute> findByCatalogAndUsage(final long catalogUid, final int attributeUsageId);

}