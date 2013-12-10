/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.exception.EpBindException;

/**
 * An AttributeValueGroup is a convenience container object for a collection of <code>AttributeValue</code>s; 
 * it provides accessor methods for the AttributeValues contained within it. 
 * It's designed to make it easier to work with locale-specific AttributeValues that are associated with a Product, Catalog, etc.
 * The AttributeValues that a domain object can have depend on the Type of the domain object.
 * For example, a ProductType=Camera might have an Attribute=Megapixels, and an AttributeValue=7.2.
 */
public interface AttributeValueGroup {
	/**
	 * Get the value of an attribute with the specified key in the given locale. If the attribute value of the given key and locale is not found, it
	 * will fallback to the attribute value found in the following sequence: <br> - a fall-back attribute value with the same language of the given
	 * locale <br> - a fall-back attribute value with the default locale <br> - a fall-back attribute value without locale<br>
	 * 
	 * @param attributeKey the key of the attribute to be retrieved
	 * @param locale the locale for which the attribute is requested
	 * @return the <code>AttributeValue</code>
	 */
	AttributeValue getAttributeValue(final String attributeKey, final Locale locale);

	/**
	 * Get the value of a string attribute with the specified key in the given locale.
	 * 
	 * @param attributeKey the key of the attribute to be retrieved
	 * @param locale the locale for which the attribute is requested
	 * @return the <code>String</code> value of the attribute
	 */
	String getStringAttributeValue(final String attributeKey, final Locale locale);

	/**
	 * Get the list of <code>AttributeValue</code>s for all attributes defined in the given <code>AttributeGroup</code>,
	 * ensuring that an AttributeValue is returned for every Attribute. In cases where an Attribute is
	 * locale-dependent, if an AttributeValue doesn't exist for a particular supported locale 
	 * then a new empty AttributeValue will be created for that attribute and locale.
	 *
	 * @param attributeGroup the attribute group
	 * @param supportedLocales the locales for which AttributeValues are expected in the case of locale-dependent attributes
	 * @return a list of <code>AttributeValue</code>s
	 */
	List<AttributeValue> getFullAttributeValues(final AttributeGroup attributeGroup, final Collection<Locale> supportedLocales);

	/**
	 * Get the list of <code>AttributeValue</code>s for all attributes defined in the given <code>AttributeGroup</code>.
	 * In the case of locale-dependent attributes, only returns the AttributeValue applicable to the given Locale. If 
	 * an AttributeValue does not exist for the locale-dependent Attribute and given Locale, then a new empty AttributeValue
	 * will be created for that attribute and locale.
	 * 
	 * @param attributeGroup the attribute group
	 * @param locale the locale in which to return ant locale-dependent attributes
	 * @return a list of <code>AttributeValue</code>s
	 */
	List<AttributeValue> getFullAttributeValues(AttributeGroup attributeGroup, Locale locale);
	
	/**
	 * Get a list of <code>AttributeValue</code>s with the given locale for attributes defined in the given attribute group. If an attribute
	 * in the given attribute group has a value, the value will be returned. Otherwise, it won't be returned in the list.
	 * 
	 * @param attributeGroup the attribute group
	 * @param locale the locale
	 * @return a list of <code>AttributeValue</code>s
	 */
	List<AttributeValue> getAttributeValues(AttributeGroup attributeGroup, Locale locale);

	/**
	 * Set the attribute value of the domain object.
	 * 
	 * @param attribute the attribute to set the value
	 * @param locale the locale of the value, set it to <code>null</code> if it's not locale-dependant
	 * @param value the value
	 */
	void setAttributeValue(Attribute attribute, Locale locale, Object value);

	/**
	 * Set the attribute value of the domain object based on the given string value.
	 * 
	 * @param attribute the attribute to set the value
	 * @param locale the locale of the value, set it to <code>null</code> if it's not locale-dependant
	 * @param stringValue the string value
	 * @throws EpBindException in case the given string value is invalid
	 */
	void setStringAttributeValue(Attribute attribute, Locale locale, String stringValue) throws EpBindException;

	/**
	 * Sets the attribute value map.
	 * 
	 * @param attributeValueMap the attribute value map
	 */
	void setAttributeValueMap(Map<String, AttributeValue> attributeValueMap);

	/**
	 * Returns the attribute value map.
	 * 
	 * @return the attribute value map
	 */
	Map<String, AttributeValue> getAttributeValueMap();

	/**
	 * Removes all attribute values that match incoming attribute key. If incoming attribute is locale dependant, all matching attribute values will
	 * be removed.
	 * 
	 * @param attToRemove attribute to remove.
	 */
	void removeByAttribute(Attribute attToRemove);

	/**
	 * Removes all attribute values that match incoming attribute keys. If incoming attribute is locale dependant, all matching attribute values will
	 * be removed.
	 * 
	 * @param toRemove contains all attributes to remove.
	 */
	void removeByAttributes(final Set<Attribute> toRemove);

	/**
 	 * Returns the factory used to create new AttributeValues in this group.
 	 *
	 * @return the attribute value factory.
	 */
	AttributeValueFactory getAttributeValueFactory();
}
