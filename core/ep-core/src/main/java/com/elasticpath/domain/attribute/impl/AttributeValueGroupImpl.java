/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.base.Initializable;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.util.AttributeComparator;
import com.elasticpath.commons.util.impl.LocaleUtils;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;

/**
 * An AttributeValueGroup is a convenience container object for a collection of <code>AttributeValue</code>s; 
 * it provides accessor methods for the AttributeValues contained within it. 
 * It's designed to make it easier to work with locale-specific AttributeValues that are associated with a Product, Catalog, etc.
 * The AttributeValues that a domain object can have depend on the Type of the domain object.
 * For example, a ProductType=Camera might have an Attribute=Megapixels, and an AttributeValue=7.2.
 */
public class AttributeValueGroupImpl extends AbstractLegacyPersistenceImpl implements AttributeValueGroup, Initializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final char SEPARATOR = '_';

	/** Map of localized attribute keys to attribute values. */
	private Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();

	private final AttributeValueFactory attributeValueFactory;

	private long uidPk;

	/**
	 * Constructor.
	 * @param factory the AttributeValueFactory used to construct AttributeValue objects
	 */
	public AttributeValueGroupImpl(final AttributeValueFactory factory) {
		this.attributeValueFactory = factory;
	}

	/**
	 * Get the value of an attribute with the specified key in the given locale.
	 * If the attribute with the specified key does not exist in the given locale
	 * then this will attempt to find the attribute value without using a locale.
	 * 
	 * May return null if no attribute value is ultimately found.
	 * 
	 * This implementation delegates to getAttributeValueWithoutFallback(key, locale).
	 *
	 * @param attributeKey the key of the attribute to be retrieved
	 * @param locale the locale for which the attribute is requested
	 * @return the <code>AttributeValue</code>, or null if it cannot be found
	 */
	@Override
	public AttributeValue getAttributeValue(final String attributeKey, final Locale locale) {		
		return getAttributeValueWithoutFallBack(attributeKey, locale);		
	}

	/**
	 * Get the value of a string attribute with the specified key in the given locale.
	 *
	 * @param attributeKey the key of the attribute to be retrieved
	 * @param locale the locale for which the attribute is requested
	 * @return the <code>String</code> value of the attribute
	 */
	@Override
	public String getStringAttributeValue(final String attributeKey, final Locale locale) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, locale);
		if (attributeValue == null) {
			return "";
		}

		return attributeValue.getStringValue();
	}	

	/**
	 * Set the attribute value of the domain object.
	 *
	 * @param attribute the attribute to set the value
	 * @param locale the locale of the value, set it to <code>null</code> if it's not locale-dependant
	 * @param value the value
	 */
	@Override
	public void setAttributeValue(final Attribute attribute, final Locale locale, final Object value) {
		AttributeValue attributeValue = getExactAttributeValue(attribute.getKey(), locale);
		if (attributeValue != null) {
			attributeValue.setValue(value);
			return;
		}

		attributeValue = createAttributeValue(attribute, locale);
		attributeValue.setValue(value);
		attributeValueMap.put(attributeValue.getLocalizedAttributeKey(), attributeValue);
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		if (this.attributeValueMap == null) {
			this.attributeValueMap = new HashMap<String, AttributeValue>();
		}
	}

	/**
	 * Set the attribute value of the domain object based on the given string value.
	 *
	 * @param attribute the attribute to set the value
	 * @param locale the locale of the value, set it to <code>null</code> if it's not locale-dependant
	 * @param stringValue the string value
	 * @throws EpBindException in case the given string value is invalid
	 */
	@Override
	public void setStringAttributeValue(final Attribute attribute, final Locale locale, final String stringValue) throws EpBindException {
		AttributeValue attributeValue = getExactAttributeValue(attribute.getKey(), locale);
		if (attributeValue != null) {
			attributeValue.setStringValue(stringValue);
			return;
		}

		attributeValue = createAttributeValue(attribute, locale);
		attributeValue.setStringValue(stringValue);
		attributeValueMap.put(attributeValue.getLocalizedAttributeKey(), attributeValue);
	}

	/**
	 * Sets the attribute value map.
	 *
	 * @param attributeValueMap the attribute value map
	 */
	@Override
	public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		this.attributeValueMap = attributeValueMap;
	}

	/**
	 * Returns the attribute value map.
	 *
	 * @return the attribute value map
	 */
	@Override
	public Map<String, AttributeValue> getAttributeValueMap() {
		return this.attributeValueMap;
	}

	/**
	 * Get the list of <code>AttributeValue</code>s for all attributes defined in the given attribute group,
	 * ensuring that an AttributeValue is returned for every Attribute. In cases where an Attribute is
	 * locale-dependent, if an AttributeValue doesn't exist for a particular supported locale 
	 * then a new empty AttributeValue will be created for that attribute and locale.
	 *
	 * @param attributeGroup the attribute group
	 * @param supportedLocales the locales for which AttributeValues are expected in the case of locale-dependent attributes
	 * @return a list of <code>AttributeValue</code>s
	 */
	@Override
	public List<AttributeValue> getFullAttributeValues(final AttributeGroup attributeGroup, final Collection<Locale> supportedLocales) {
		final List<AttributeValue> fullAttributeValues = new ArrayList<AttributeValue>();

		for (AttributeGroupAttribute attributeGroupAttribute : attributeGroup.getAttributeGroupAttributes()) {
			final Attribute attribute = attributeGroupAttribute.getAttribute();
			final String attributeKey = attribute.getKey();
			if (attribute.isLocaleDependant()) {
				for (Locale locale : supportedLocales) {
					AttributeValue attributeValue = getAttributeValueWithoutFallBack(attributeKey, locale);
					if (attributeValue == null) {
						attributeValue = createAttributeValue(attribute, locale);
					}

					fullAttributeValues.add(attributeValue);
				}
			} else {
				AttributeValue attributeValue = getAttributeValueWithoutFallBack(attributeKey, null);
				if (attributeValue == null) {
					attributeValue = createAttributeValue(attribute, null);
				}
				fullAttributeValues.add(attributeValue);
			}
		}

		return fullAttributeValues;
	}

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
	@Override
	public List<AttributeValue> getFullAttributeValues(final AttributeGroup attributeGroup, final Locale locale) {
		final List<AttributeValue> fullAttributeValues = new ArrayList<AttributeValue>();
	    Set<AttributeGroupAttribute> attributeGroupAttributes = new TreeSet<AttributeGroupAttribute>(new AttributeComparator());
	    attributeGroupAttributes.addAll(attributeGroup.getAttributeGroupAttributes());		

	    for (AttributeGroupAttribute attributeGroupAttribute : attributeGroupAttributes) {
			final Attribute attribute = attributeGroupAttribute.getAttribute();
			final String attributeKey = attribute.getKey();
			if (attribute.isLocaleDependant()) {
				AttributeValue attributeValue = getAttributeValueWithoutFallBack(attributeKey, locale);
				if (attributeValue == null) {
					attributeValue = createAttributeValue(attribute, locale);
				}
				fullAttributeValues.add(attributeValue);
			} else {
				AttributeValue attributeValue = getAttributeValueWithoutFallBack(attributeKey, null);
				if (attributeValue == null) {
					attributeValue = createAttributeValue(attribute, null);
				}
				fullAttributeValues.add(attributeValue);
			}
		}

		return fullAttributeValues;
	}

	/**
	 * Get a list of <code>AttributeValue</code>s with the given locale for 
	 * attributes defined in the given attribute group. If an attribute in
	 * the given attribute group has a value, the value will be returned. 
	 * Otherwise, it won't be returned in the list.
	 *
	 * @param attributeGroup the attribute group
	 * @param locale the locale
	 * @return a list of <code>AttributeValue</code>s
	 */
	@Override
	public List<AttributeValue> getAttributeValues(final AttributeGroup attributeGroup, final Locale locale) {
		final List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();

		for (AttributeGroupAttribute attributeGroupAttribute : attributeGroup.getAttributeGroupAttributes()) {
			final Attribute attribute = attributeGroupAttribute.getAttribute();
			final String attributeKey = attribute.getKey();
			if (attribute.isLocaleDependant()) {
				AttributeValue attributeValue = getAttributeValueWithoutFallBack(attributeKey, locale);
				if (attributeValue != null) {
					attributeValues.add(attributeValue);
				}
			} else {
				AttributeValue attributeValue = getAttributeValueWithoutFallBack(attributeKey, null);
				if (attributeValue != null) {
					attributeValues.add(attributeValue);
				}
			}
		}

		return attributeValues;
	}

	/**
	 * Removes all attribute values that match incoming attribute key. 
	 * If incoming attribute is locale-dependent, all matching attribute values will
	 * be removed.
	 *
	 * @param attToRemove attribute to remove.
	 */
	@Override
	public void removeByAttribute(final Attribute attToRemove) {
		final String attributeKey = attToRemove.getKey();
		if (attToRemove.isLocaleDependant()) {
			for (Locale locale : Locale.getAvailableLocales()) {
				removeAttributeValueWithoutFallBack(attributeKey, locale);
			}
		} else {
			removeAttributeValueWithoutFallBack(attributeKey, null);
		}
	}

	/**
	 * Removes all attribute values that match incoming attribute keys. 
	 * If incoming attribute is locale-dependent, all matching attribute values will
	 * be removed.
	 *
	 * @param toRemove contains all attributes to remove.
	 */
	@Override
	public void removeByAttributes(final Set<Attribute> toRemove) {
		for (Iterator<Attribute> i = toRemove.iterator(); i.hasNext();) {
			removeByAttribute(i.next());
		}
	}

	private AttributeValue createAttributeValue(final Attribute attribute, final Locale locale) {
		return attributeValueFactory.createAttributeValue(
				attribute, getLocalizedAttributeKey(attribute.getKey(), locale));
	}

	/**
	 * From the AttributeValueMap this will find the AttributeValue that corresponds to an Attribute key
	 * that is composed of the given key and the given locale. This implementation will find the most
	 * specific key for the given locale, trying the full locale (language, country, variant) before falling 
	 * back to just the language and country, then finally to just the language.
	 * 
	 * If the provided locale is null, will attempt to return the attribute value with a key
	 * that is not locale-specific, which may return null.
	 * 
	 * @param attributeKey the non-localized portion of the attribute key
	 * @param locale the locale to combine with the attribute key to form the localized key
	 * @return the AttributeValue matching the key and given locale, or null if not found
	 */
	protected AttributeValue getAttributeValueWithoutFallBack(final String attributeKey, final Locale locale) {
		if (locale == null) {
			return getAttributeValueMap().get(getLocalizedAttributeKey(attributeKey, null));
		}
		Locale broadenedLocale = locale;
		
		AttributeValue attributeValue = getExactAttributeValue(attributeKey, broadenedLocale);
		if (attributeValue == null) { //Remove the variant, if present
			broadenedLocale = LocaleUtils.broadenLocale(broadenedLocale);
			attributeValue = getExactAttributeValue(attributeKey, broadenedLocale);
		}
		if (attributeValue == null) { //Remove the country, if present
			broadenedLocale = LocaleUtils.broadenLocale(broadenedLocale);
			attributeValue = getExactAttributeValue(attributeKey, broadenedLocale);
		}
		//As a last resort, use the key without a locale suffix.
		if (attributeValue == null) {
			attributeValue = getAttributeValueMap().get(attributeKey);
		}
		return attributeValue;
	}

	/**
	 * Gets the exact attribute value for the given key and locale.
	 *
	 * @param attributeKey the attribute key
	 * @param locale the locale
	 * @return the exact attribute value or null if not found
	 */
	protected AttributeValue getExactAttributeValue(final String attributeKey, final Locale locale) {
		return getAttributeValueMap().get(getLocalizedAttributeKey(attributeKey, locale));
	}

	private void removeAttributeValueWithoutFallBack(final String attributeKey, final Locale locale) {
		final String localizedAttributeKey = getLocalizedAttributeKey(attributeKey, locale);
		this.attributeValueMap.remove(localizedAttributeKey);
	}

	/**
	 * Creates a localized attribute key by combining the given key and the given locale.
	 * @param key the static portion of the AttributeKey
	 * @param locale the locale to use in creating the locale portion of the AttributeKey
	 * @return an aggregate key to look up a localized AttributeValue in the AttributeValueMap
	 */
	protected String getLocalizedAttributeKey(final String key, final Locale locale) {
		if (locale == null) {
			return key;
		}
		return key + SEPARATOR + locale.toString();		
	}
	
	/**
	 * String representation of this object.
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("attributeValueMap", getAttributeValueMap())
				.toString();
	}

	/**
	 * Returns the factory used to create new AttributeValues in this group.
	 *
	 * @return the attribute value factory.
	 */
	@Override
	public AttributeValueFactory getAttributeValueFactory() {
		return attributeValueFactory;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@Transient
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}
