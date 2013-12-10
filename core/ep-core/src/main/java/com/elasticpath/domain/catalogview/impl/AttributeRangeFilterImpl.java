/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.RangeFilterType;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Default implementation of {@link AttributeRangeFilter}.
 */
public class AttributeRangeFilterImpl extends AbstractRangeFilterImpl<AttributeRangeFilter, AttributeValueWithType> implements
		AttributeRangeFilter {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private Attribute attribute;

	private String attributeKey;

	private Locale locale;

	/**
	 * Returns <code>true</code> if this filter equals to the given object.
	 *
	 * @param object the object to compare
	 * @return <code>true</code> if this filter equals to the given object.
	 */
	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof AttributeRangeFilter)) {
			return false;
		}
		return getId().equals(((AttributeRangeFilter) object).getId());
	}

	/**
	 * Returns the hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	/**
	 * Get the locale for this attribute.
	 *
	 * @return the locale
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Set the locale for this attribute.
	 *
	 * @param locale the locale to set
	 */
	@Override
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * Get the attribute key.
	 *
	 * @return the attributeKey
	 */
	@Override
	public String getAttributeKey() {
		return attributeKey;
	}

	/**
	 * Set the attribute Key.
	 *
	 * @param attributeKey the attributeKey to set
	 */
	@Override
	public void setAttributeKey(final String attributeKey) {
		this.attributeKey = attributeKey;
		final AttributeService attributeService = getBean(ContextIdNames.ATTRIBUTE_SERVICE);
		attribute = attributeService.findByKey(attributeKey);
	}

	/**
	 * @return the attribute
	 */
	@Override
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	@Override
	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
		attributeKey = attribute.getKey();
	}

	/**
	 * Returns the display name of the filter with the given locale.
	 *
	 * @param locale the locale
	 * @return the display name of the filter with the given locale.
	 */
	@Override
	public String getDisplayName(final Locale locale) {

		String displayname = super.getDisplayName(locale);

		if (displayname != null) {
			return displayname;
		}
		
		if (getRangeType() == RangeFilterType.ALL && getAttribute() == null && getAttributeKey() == null) {
			throw new EpDomainException("Filter not initialized");
		}

		if (getRangeType() == RangeFilterType.BETWEEN) {
			displayname = getLowerValue() + " - " + getUpperValue();
		} else if (getRangeType() == RangeFilterType.LESS_THAN) {
			displayname = "< " + getUpperValue();
		} else if (getRangeType() == RangeFilterType.MORE_THAN) {
			displayname = "> " + getLowerValue();
		}

		return displayname;
	}

	/**
	 * This method returns the seo id.
	 *
	 * @return the SEO identifier of the filter with the given locale.
	 */
	@Override
	public String getSeoId() {
		StringBuilder seoString = new StringBuilder(getAttributePrefixAndKey());
		seoString.append(getSeparatorInToken());
		seoString.append(getAlias());
		return seoString.toString();
	}
	
	@Override
	public String getAttributePrefixAndKey() {
		StringBuilder seoString = new StringBuilder(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX);
		seoString.append(getAttributeKey());
		return seoString.toString();
	}
	
	private AttributeValueWithType constructAttributeWithType(final String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
		final AttributeValueWithType attr = getBean(ContextIdNames.ATTRIBUTE_VALUE);
		attr.setAttribute(getAttribute());
		attr.setAttributeType(getAttribute().getAttributeType());
		attr.setStringValue(string);
		return attr;
	}

	@Override
	public void initialize(final Map<String, Object> properties) {
		if (properties.get(ATTRIBUTE_PROPERTY) == null) {
			this.setAttributeKey((String) properties.get(ATTRIBUTE_KEY_PROPERTY));
		} else {
			this.setAttribute((Attribute) properties.get(ATTRIBUTE_PROPERTY));
		}
		AttributeValueWithType lowerValue = constructAttributeWithType((String) properties.get(LOWER_VALUE_PROPERTY));
		AttributeValueWithType upperValue = constructAttributeWithType((String) properties.get(UPPER_VALUE_PROPERTY));
		this.setLowerValue(lowerValue);
		this.setUpperValue(upperValue);
		if (lowerValue != null && upperValue != null && lowerValue.compareTo(upperValue) > 0) {
			throw new EpCatalogViewRequestBindException("Invalid filter id: " + getSeoId());
		}
		this.setAlias((String) properties.get(ATTRIBUTE_VALUES_ALIAS_PROPERTY));
		this.setId(getSeoId());
	}

	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		final String idWithoutPrefix = filterIdStr.substring(filterIdStr.indexOf(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX)
				+ SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX.length());
		final String[] tokens = idWithoutPrefix.split(getSeparatorInToken());
		
		// -1 to check for tokens that don't defined an upper bound
		if (tokens.length != RANGE_TOKENS && tokens.length != RANGE_TOKENS - 1) {
			throw new EpCatalogViewRequestBindException("Invalid filter id: " + filterIdStr);
		}
		try {
			tokenMap.put(ATTRIBUTE_KEY_PROPERTY, tokens[0]);
			
			String lowerValue = tokens[LOWER_VALUE_POSITION];
			tokenMap.put(LOWER_VALUE_PROPERTY, lowerValue);
			
			String upperValue = null;
			if (tokens.length - 1 >= UPPER_VALUE_POSITION) {
				upperValue = tokens[UPPER_VALUE_POSITION];
			}
			tokenMap.put(UPPER_VALUE_PROPERTY, upperValue);
			
			StringBuilder aliasString = new StringBuilder();
			if (lowerValue != null) {
				aliasString.append(lowerValue);
			}
			if (upperValue != null) {
				aliasString.append(getSeparatorInToken());
				aliasString.append(upperValue);
			}
			tokenMap.put(ATTRIBUTE_VALUES_ALIAS_PROPERTY, aliasString.toString());

		} catch (RuntimeException e) {
			throw new EpCatalogViewRequestBindException("Invalid filter id: " + filterIdStr, e);
		}
		return tokenMap;
	}
}
