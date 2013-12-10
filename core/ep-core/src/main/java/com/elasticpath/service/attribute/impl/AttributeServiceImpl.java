package com.elasticpath.service.attribute.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.persistence.support.DistinctAttributeValueCriterion;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Default implementation for <code>AttributeService</code>.
 */
public class AttributeServiceImpl extends AbstractEpPersistenceServiceImpl implements AttributeService {

	private static final String ATTRIBUTE_FIND_BY_USAGE = "ATTRIBUTE_FIND_BY_USAGE";
	private static final String ATTRIBUTE_FIND_BY_USAGE_IDS = "ATTRIBUTE_FIND_BY_USAGE_IDS";

	private DistinctAttributeValueCriterion distinctAttributeValueCriterion;

	/**
	 * Adds the given attribute.
	 * 
	 * @param attribute the attribute to add
	 * @return the persisted instance of attribute
	 * @throws DuplicateKeyException - if the specified attribute key is already in use.
	 */
	public Attribute add(final Attribute attribute) throws DuplicateKeyException {
		sanityCheck();
		if (keyExists(attribute.getKey())) {
			throw new DuplicateKeyException("Attribute with the key \"" + attribute.getKey() + "\" already exists.");
		}

		if (this.nameExistsInAttributeUsage(attribute.getName(), attribute.getAttributeUsage())) {
			throw new DuplicateKeyException("Attribute with the name \"" + attribute.getName() + "\" already exists.");
		}

		getPersistenceEngine().save(attribute);

		return attribute;
	}

	/**
	 * Updates the given attribute.
	 * 
	 * @param attribute the attribute to update
	 * @return the updated attribute instance
	 * @throws DuplicateKeyException - if the speicifed attribute key is already in use.
	 */
	public Attribute update(final Attribute attribute) throws DuplicateKeyException {
		sanityCheck();
		if (keyExists(attribute)) {
			throw new DuplicateKeyException("Attribute with the key \"" + attribute.getKey() + "\" already exists");
		}
		if (this.nameExistsInAttributeUsage(attribute)) {
			throw new DuplicateKeyException("Attribute with the name \"" + attribute.getName() + "\" already exists");
		}
		final Attribute updatedAttribute = getPersistenceEngine().merge(attribute);

		return updatedAttribute;
	}

	/**
	 * Delete the attribute.
	 * 
	 * @param attribute the attribute to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final Attribute attribute) throws EpServiceException {
		sanityCheck();

		getPersistenceEngine().delete(attribute);

	}

	/**
	 * Load the attribute with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * 
	 * @param attributeUid the attribute UID
	 * @return the attribute if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Attribute load(final long attributeUid) throws EpServiceException {
		sanityCheck();
		Attribute attribute = null;
		if (attributeUid <= 0) {
			attribute = getBean(ContextIdNames.ATTRIBUTE);
		} else {
			attribute = getPersistentBeanFinder().load(ContextIdNames.ATTRIBUTE, attributeUid);
		}
		return attribute;
	}

	/**
	 * Get the attribute with the given UID. Return null if no matching record exists.
	 * 
	 * @param attributeUid the attribute UID
	 * @return the attribute if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Attribute get(final long attributeUid) throws EpServiceException {
		sanityCheck();
		Attribute attribute = null;
		if (attributeUid <= 0) {
			attribute = getBean(ContextIdNames.ATTRIBUTE);
		} else {
			attribute = getPersistentBeanFinder().get(ContextIdNames.ATTRIBUTE, attributeUid);
		}
		return attribute;
	}

	/**
	 * Generic load method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Find the attribute with the given key.
	 * 
	 * @param key the attribute key.
	 * @return the attribute that matches the given key, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Attribute findByKey(final String key) throws EpServiceException {
		sanityCheck();
		if (key == null) {
			throw new EpServiceException("Cannot retrieve null key.");
		}

		final List<Attribute> results = getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_FIND_BY_KEY", key);
		Attribute attribute = null;
		if (results.size() == 1) {
			attribute = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate attribute key exist -- " + key);
		}
		return attribute;
	}

	/**
	 * Find the attribute with the given name and attribute usage.
	 * 
	 * @param name the attribute name.
	 * @param attributeUsage the attribute usage.
	 * @return the attribute that matches the given name and attribute usage, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Attribute findByNameAndUsage(final String name, final AttributeUsage attributeUsage) throws EpServiceException {
		sanityCheck();
		if (name == null || attributeUsage == null) {
			throw new EpServiceException("Cannot retrieve null name or usage.");
		}

		final List<Attribute> results = getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_FIND_BY_NAME_USAGE",
				name,
				attributeUsage.getValue());
		Attribute attribute = null;
		if (results.size() == 1) {
			attribute = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate attribute name exist -- " + name);
		}
		return attribute;
	}

	/**
	 * Checks whether the given attribute key exists or not.
	 * 
	 * @param key the attribute key.
	 * @return true if the given key exists.
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean keyExists(final String key) throws EpServiceException {
		if (key == null) {
			return false;
		}
		final Attribute attribute = this.findByKey(key);
		boolean keyExists = false;
		if (attribute != null) {
			keyExists = true;
		}
		return keyExists;
	}

	/**
	 * Check whether the given attribute's key exists or not.
	 * 
	 * @param attribute the attribute to check
	 * @return true if a different attribute with the given attribute's key exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean keyExists(final Attribute attribute) throws EpServiceException {
		if (attribute.getKey() == null) {
			return false;
		}
		final Attribute existingAttribute = this.findByKey(attribute.getKey());
		boolean keyExists = false;
		if (existingAttribute != null && existingAttribute.getUidPk() != attribute.getUidPk()) {
			keyExists = true;
		}
		return keyExists;
	}

	/**
	 * Checks whether the given attribute name exists in this attribute usage or not.
	 * 
	 * @param name the attribute name.
	 * @param attributeUsage the attribute usage.
	 * @return true if the given name exists.
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean nameExistsInAttributeUsage(final String name, final AttributeUsage attributeUsage) throws EpServiceException {
		if (name == null || attributeUsage == null) {
			return false;
		}
		final Attribute attribute = this.findByNameAndUsage(name, attributeUsage);
		boolean nameExists = false;
		if (attribute != null) {
			nameExists = true;
		}
		return nameExists;
	}

	/**
	 * Checks whether the given attribute name exists in this attribute usage or not.
	 * 
	 * @param attribute the attribute to check
	 * @return true if a different attribute with the given attribute's name exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean nameExistsInAttributeUsage(final Attribute attribute) throws EpServiceException {
		if (attribute.getName() == null) {
			return false;
		}
		final Attribute existingAttribute = this.findByNameAndUsage(attribute.getName(), attribute.getAttributeUsage());
		boolean nameExists = false;
		if (existingAttribute != null && existingAttribute.getUidPk() != attribute.getUidPk()) {
			nameExists = true;
		}
		return nameExists;
	}

	/**
	 * Lists all attribute stored in the database.
	 * 
	 * @return a list of attribute
	 * @throws EpServiceException - in case of any errors
	 */
	public List<Attribute> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_SELECT_ALL");
	}

	/**
	 * Retrieves a collection of {@link Attribute}s that are in the given catalog (via it's UID) or is a global attribute.
	 * 
	 * @param catalogUid the catalog UID
	 * @return a collection of {@link Attribute}s
	 * @throws EpServiceException in case of any errors
	 */
	public Collection<Attribute> findAllCatalogOrGlobalAttributes(final long catalogUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_SELECT_CATALOG_OR_GLOBAL_ALL", catalogUid);
	}

	/**
	 * Retrieves a collection of all {@link Attribute}s that are in the given catalog (via it's UID) 
	 * and adds up all global attributes of the given usage types.
	 * 
	 * @param catalogUid the catalog UID
	 * @param globalAttributeUsage attribute usage {@link AttributeUsage} constants in a collection used to retrieve the global attributes
	 * @return a collection of {@link Attribute}s
	 * @throws EpServiceException in case of any errors
	 */
	public Collection<Attribute> findAllCatalogAndGlobalAttributesByType(final long catalogUid, 
			final Collection<Integer> globalAttributeUsage) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQueryWithList("ATTRIBUTE_SELECT_CATALOG_OR_GLOBAL_BY_USAGE", "list",
				globalAttributeUsage,
				catalogUid);
	}

	/**
	 * Retrieves a collection of {@link Attribute}s that are global attributes.
	 * 
	 * @return a collection of {@link Attribute}s
	 * @throws EpServiceException in case of any errors
	 */
	public Collection<Attribute> findAllGlobalAttributes() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_SELECT_GLOBAL_ALL");
	}

	/**
	 * Return the map for attribute usage value-description.
	 * 
	 * @return the map for attribute usage value-description
	 */
	public Map<String, String> getAttributeUsageMap() {
		return ((AttributeUsage) getBean(ContextIdNames.ATTRIBUTE_USAGE)).getAttributeUsageMap();
	}

	/**
	 * Return the map for attribute type value-description. Use stuff from AbstractAttributeTypeImpl to reduce duplication.
	 * 
	 * @return the map for attribute type value-description
	 */
	public Map<String, String> getAttributeTypeMap() {
		Map<String, String> typeMap = new LinkedHashMap<String, String>();

		for (final AttributeType type : AttributeType.values()) {
			typeMap.put(String.valueOf(type.getTypeId()), type.getNameMessageKey());
		}

		return typeMap;
	}

	/**
	 * Return a list of uids for all attributes in use, exclude the customer profile attributes.
	 * 
	 * @return a list of uids for all attributes in use, exclude the customer profiel attributes
	 */
	public List<Long> getAttributeInUseUidList() {
		final List<Long> attributeUidList = new ArrayList<Long>();
		List<Long> queryResponse = getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_IN_USE_CATEGORY_TYPE");
		attributeUidList.addAll(queryResponse);
		queryResponse = getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_IN_USE_PRODUCT_TYPE");
		attributeUidList.addAll(queryResponse);
		queryResponse = getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_IN_USE_PRODUCT_TYPE_SKU");
		attributeUidList.addAll(queryResponse);
		return attributeUidList;
	}

	/**
	 * Return a list of uids for all customer profile attributes in use.
	 * 
	 * @return a list of uids for all customer profile attributes in use
	 */
	public List<Long> getCustomerProfileAttributeInUseUidList() {
		return getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_IN_USE_CUSTOMER_PROFILE_TYPE");

	}

	/**
	 * Return a list of category attributes.
	 * 
	 * @return a list of category attributes.
	 */
	public List<Attribute> getCategoryAttributes() {
		return getAttributes(AttributeUsage.CATEGORY);
	}

	/**
	 * Return a list of attributes used for products.
	 * 
	 * @return a list of products attributes.
	 */
	public List<Attribute> getProductAttributes() {
		return getAttributes(AttributeUsage.PRODUCT);
	}

	/**
	 * Return a list of attributes used for skus.
	 * 
	 * @return a list of sku attributes.
	 */
	public List<Attribute> getSkuAttributes() {
		return getAttributes(AttributeUsage.SKU);
	}
	
	/**
	 * For a given attribute, the set of distinct values currently existing for that attribute is returned.
	 * 
	 * @param attribute the attribute for which the distinct attribute values are to be returned
	 * @param languageCode the lower case code of the language for which attributes are to be returned.
	 * @return a List of matching <code>AttributeValue</code>s
	 */
	public List<String> getDistinctAttributeValueList(final Attribute attribute, final String languageCode) {
		// TA67 Support multi-values for short text type.
		if ((attribute.isMultiValueEnabled()) //
		// && (attribute.getAttributeType().getTypeId() == AbstractAttributeTypeImpl.SHORT_TEXT_TYPE_ID)
		// && (attribute.getAttributeUsage().getValue() == AttributeUsage.PRODUCT)
		) {
			return getDistinctAttributeMultiValueList(attribute, languageCode);
		}
		return getDistinctAttributeSingleValueList(attribute, languageCode);
	}

	private List<String> getDistinctAttributeSingleValueList(final Attribute attribute, final String languageCode) {
		final List<String> distinctValues = new ArrayList<String>();

		List<Object[]> tuples = getPersistenceEngine().retrieve(distinctAttributeValueCriterion.getDistinctAttributeValueCriterion(attribute));

		for (Object[] currTuple : tuples) {
			Object value = currTuple[0];
			String localizedKey = (String) currTuple[1];

			if ("".equals(languageCode) || !attribute.isLocaleDependant() || localizedKey.endsWith(languageCode)) {
				distinctValues.add(value.toString());
			}
		}

		return distinctValues;
	}

	private List<String> getDistinctAttributeMultiValueList(final Attribute attribute, final String languageCode) {
		Map<String, Object> distinctMap = new HashMap<String, Object>();
		final List<String> distinctValues = new ArrayList<String>();

		List<Object[]> tuples = getPersistenceEngine().retrieve(distinctAttributeValueCriterion.getDistinctAttributeMultiValueCriterion(attribute));

		for (Object[] currTuple : tuples) {
			Object value = currTuple[0];
			String localizedKey = (String) currTuple[1];

			if ("".equals(languageCode) || !attribute.isLocaleDependant() || localizedKey.endsWith(languageCode)) {
				// distinctValues.add(value.toString());
				StringTokenizer stValues = new StringTokenizer(value.toString(), ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPERATOR);
				if (stValues.hasMoreTokens()) {
					while (stValues.hasMoreTokens()) {
						String singleValue = stValues.nextToken().trim();
						if (singleValue.length() > 0) {
							distinctMap.put(singleValue, null);
						}
					}
				}
			}
		}
		distinctValues.addAll(distinctMap.keySet());
		return distinctValues;
	}

	/**
	 * Sets the generator for criteria used to query the persistence layer for distinct lists of attribute values.
	 * 
	 * @param distinctAttributeValueCriterion the <code>DistinctAttributeValueCriterion</code>
	 */
	public void setDistinctAttributeValueCriterion(final DistinctAttributeValueCriterion distinctAttributeValueCriterion) {
		this.distinctAttributeValueCriterion = distinctAttributeValueCriterion;
	}

	/**
	 * Returns a map of all system attributes.
	 * 
	 * @return a map of all system attributes
	 */
	public Map<String, Attribute> getCustomerProfileAttributesMap() {
		Map<String, Attribute> customerProfileAttributesMap = new LinkedHashMap<String, Attribute>();
		List<Attribute> customerProfileAttributesList = getCustomerProfileAttributes();
		for (Attribute attribute : customerProfileAttributesList) {
			customerProfileAttributesMap.put(attribute.getKey(), attribute);
		}
		return customerProfileAttributesMap;
	}

	/**
	 * Returns a list of customer profile attributes.
	 * 
	 * @return a list of customer profile attributes
	 */
	public List<Attribute> getCustomerProfileAttributes() {
		return getAttributes(AttributeUsage.CUSTOMERPROFILE);
	}

	/**
	 * Returns a list of attributes exclude customer profile attribute.
	 * 
	 * This Method Really is looking for ALL Product Related Attributes.
	 * 
	 * @return a list of attributes exclude customer profile attribute
	 */
	public List<Attribute> getAttributesExcludeCustomerProfile() {
		final Collection<Integer> productAttributeUsage = new ArrayList<Integer>();
		productAttributeUsage.add(AttributeUsage.CATEGORY);
		productAttributeUsage.add(AttributeUsage.PRODUCT);
		productAttributeUsage.add(AttributeUsage.SKU);
		return getPersistenceEngine().retrieveByNamedQueryWithList(ATTRIBUTE_FIND_BY_USAGE_IDS, "list", productAttributeUsage);
	}

	/**
	 * Return the list of attributes used for ALL But a particular AttributeUsage.
	 * 
	 * @param usage The AttributeUsage.
	 * @return the List of Attributes.
	 */
	public List<Attribute> getAttributes(final AttributeUsage usage) {
		return getAttributes(usage.getValue());
	}

	private List<Attribute> getAttributes(final int usageId) {
		return getPersistenceEngine().retrieveByNamedQuery(ATTRIBUTE_FIND_BY_USAGE, Integer.valueOf(usageId));
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
		boolean result = false;
		result |= !getPersistenceEngine().retrieveByNamedQuery("PRODUCT_ATTRIBUTE_IN_USE", uidToCheck).isEmpty();
		result |= !getPersistenceEngine().retrieveByNamedQuery("CATEGORY_ATTRIBUTE_IN_USE", uidToCheck).isEmpty();
		result |= !getPersistenceEngine().retrieveByNamedQuery("SKU_ATTRIBUTE_IN_USE", uidToCheck).isEmpty();
		result |= !getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_ATTRIBUTE_IN_USE", uidToCheck).isEmpty();
		
		return result;
	}

	/**
	 * Find the attribute with the given catalog and attribute usage.
	 * 
	 * @param catalogUid the catalog uidPk
	 * @param attributeUsageId the attributeUsage id
	 * @return List of attributes by the given catalog and attribute usage id
	 */
	public List<Attribute> findByCatalogAndUsage(final long catalogUid, final int attributeUsageId) {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("ATTRIBUTE_FIND_BY_CATALOG_USAGE", catalogUid, attributeUsageId);
	}
}
