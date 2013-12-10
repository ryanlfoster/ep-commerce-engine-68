/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.util.impl.LocaleUtils;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.service.attribute.AttributeService;

/**
 * This is a default implementation of <code>CustomerProfile</code>.
 */
public class CustomerProfileImpl extends AbstractLegacyPersistenceImpl implements CustomerProfile {

	private static final long serialVersionUID = 5000000001L;

	private static final char SEPARATOR = '_';

	private Map<String, AttributeValue> profileValueMap = new HashMap<String, AttributeValue>();

	private String profileValueBeanId;

	private long uidPk;

	@Override
	public boolean isProfileValueRequired(final String attributeKey) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, null);

		if (attributeValue == null) {
			return false;
		}

		return attributeValue.getAttribute().isRequired();
	}

	@Override
	public String getStringProfileValue(final String attributeKey) {
		// customer profile attribute is not locale dependent, set locale to null
		return getStringAttributeValue(attributeKey, null);
	}

	private String getStringAttributeValue(final String attributeKey, final Locale locale) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, locale);

		if (attributeValue == null) {
			return null;
		}

		return attributeValue.getStringValue();
	}

	@Override
	public Object getProfileValue(final String attributeKey) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, null);
		if (attributeValue == null) {
			return null;
		}
		return attributeValue.getValue();
	}

	/**
	 * Get the value of an attribute with the specified key in the given locale.<br>
	 * If the attribute with the specified key does not exist in the given locale then this will attempt to find the attribute value without using a
	 * locale.<br>
	 * May return null if no attribute value is ultimately found.<br>
	 * This implementation delegates to getAttributeValueWithoutFallback(key, locale).
	 *
	 * @param attributeKey the key of the attribute to be retrieved
	 * @param locale the locale for which the attribute is requested
	 * @return the <code>AttributeValue</code>, or null if it cannot be found
	 */
	private AttributeValue getAttributeValue(final String attributeKey, final Locale locale) {
		return getAttributeValueWithoutFallBack(attributeKey, locale);
	}

	@Override
	public void setStringProfileValue(final String attributeKey, final String stringValue) throws EpBindException {
		setStringAttributeValue(getCustomerProfileAttributesMap().get(attributeKey), null, stringValue);
	}

	private void setStringAttributeValue(final Attribute attribute, final Locale locale, final String stringValue) throws EpBindException {
		AttributeValue attributeValue = getAttributeValueWithoutFallBack(attribute.getKey(), locale);
		if (attributeValue != null) {
			if ((stringValue == null && attributeValue.getStringValue() != null)
					|| ((stringValue != null) && (!stringValue.equals(attributeValue.getStringValue())))) {
				attributeValue.setStringValue(stringValue);
			}
			return;
		}

		attributeValue = createAttributeValue(attribute);
		attributeValue.setStringValue(stringValue);
		final String localizedAttributeKey = getLocalizedAttributeKey(attribute.getKey(), locale);
		attributeValue.setLocalizedAttributeKey(localizedAttributeKey);
		getProfileValueMap().put(localizedAttributeKey, attributeValue);
	}

	@Override
	public void setProfileValue(final String attributeKey, final Object value) {
		setAttributeValue(getCustomerProfileAttributesMap().get(attributeKey), null, value);
	}

	private Map<String, Attribute> getCustomerProfileAttributesMap() {
		return this.<AttributeService>getBean(ContextIdNames.ATTRIBUTE_SERVICE).getCustomerProfileAttributesMap();
	}

	private void setAttributeValue(final Attribute attribute, final Locale locale, final Object value) {
		AttributeValue attributeValue = getAttributeValueWithoutFallBack(attribute.getKey(), locale);
		if (attributeValue != null) {
			attributeValue.setValue(value);
			return;
		}

		attributeValue = createAttributeValue(attribute);
		attributeValue.setValue(value);
		final String localizedAttributeKey = getLocalizedAttributeKey(attribute.getKey(), locale);
		attributeValue.setLocalizedAttributeKey(localizedAttributeKey);
		getProfileValueMap().put(localizedAttributeKey, attributeValue);
	}

	private AttributeValue getAttributeValueWithoutFallBack(final String attributeKey, final Locale locale) {
		if (locale == null) {
			return getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, null));
		}
		Locale broadenedLocale = locale;

		AttributeValue attributeValue = getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, broadenedLocale));
		if (attributeValue == null) { // Remove the variant, if present
			broadenedLocale = LocaleUtils.broadenLocale(broadenedLocale);
			attributeValue = getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, broadenedLocale));
		}
		if (attributeValue == null) { // Remove the country, if present
			broadenedLocale = LocaleUtils.broadenLocale(broadenedLocale);
			attributeValue = getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, broadenedLocale));
		}
		return attributeValue;
	}

	private AttributeValue createAttributeValue(final Attribute attribute) {
		AttributeValue attributeValue = getBean(profileValueBeanId);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(attribute.getAttributeType());
		return attributeValue;
	}

	@Override
	public void setProfileValueMap(final Map<String, AttributeValue> profileValueMap) {
		this.profileValueMap = profileValueMap;
	}

	@Override
	public Map<String, AttributeValue> getProfileValueMap() {
		return profileValueMap;
	}

	@Override
	public String getProfileValueBeanId() {
		return profileValueBeanId;
	}

	@Override
	public void setProfileValueBeanId(final String profileValueBeanId) {
		this.profileValueBeanId = profileValueBeanId;
	}

	@Override
	@Transient
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Creates a localized attribute key by combining the given key and the given locale.
	 *
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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("profileValueMap", getProfileValueMap())
			.toString();
	}
}
