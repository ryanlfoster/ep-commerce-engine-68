package com.elasticpath.domain.misc.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.commons.util.impl.LocaleUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;

/**
 * Represents a default implementation of <code>LocalizedProperties</code>.
 */
public class LocalizedPropertiesImpl extends AbstractEpDomainImpl implements LocalizedProperties {

	private static final long serialVersionUID = 5000000001L;

	private static final char SEPARATOR = '_';

	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private String localizedPropertyValueBean;

	/**
	 * Gets the value of the given property for the given locale.
	 * This implementation will attempt to get the property string for the given
	 * locale, and will broaden the search if necessary by removing the locale's
	 * variant and then the locale's country. If no value can be found for the
	 * given locale's language, will return null.
	 *
	 * @param propertyName the property name
	 * @param locale the locale
	 * @return the value of the given property for the given locale, or for
	 * the system locale if it doesn't exist for the given locale,
	 * or null if the value cannot be found.
	 */
	@Override
	public String getValue(final String propertyName, final Locale locale) {
		if (propertyName == null) {
			throw new EpDomainException("Property name cannot be null.");
		}
		if (locale == null) {
			throw new EpDomainException("Locale cannot be null.");
		}
		String value = getValueWithoutFallBack(propertyName, locale);

		// remove the variant, if present
		if (value == null) {
			value = getValueWithoutFallBack(propertyName, LocaleUtils.broadenLocale(locale));
		}
		// remove the country, if present
		if (value == null) {
			value = getValueWithoutFallBack(propertyName, LocaleUtils.broadenLocale(locale));
		}

		return value;
	}

	@Override
	public void setValue(final String propertyName, final Locale locale, final String value) {
		if (propertyName == null) {
			throw new EpDomainException("Property name cannot be null.");
		}
		if (locale == null) {
			throw new EpDomainException("Locale cannot be null.");
		}
		final String lpk = propertyName + SEPARATOR + locale.toString();

		final LocalizedPropertyValue wrappedValue = getNewLocalizedPropertyValue();
		wrappedValue.setLocalizedPropertyKey(lpk);
		wrappedValue.setValue(value);
		if (StringUtils.isEmpty(value)) {
			localizedPropertiesMap.remove(lpk);
		} else {
			localizedPropertiesMap.put(lpk, wrappedValue);
		}
	}

	@Override
	public Locale getLocaleFromKey(final String keyInMap) {
		String[] keys = StringUtils.split(keyInMap, SEPARATOR);
		return org.apache.commons.lang.LocaleUtils.toLocale(keys[1]);
	}

	@Override
	public String getPropertyNameFromKey(final String keyInMap) {
		String[] keys = StringUtils.split(keyInMap, SEPARATOR);
		return keys[0];
	}

	/**
	 * Creates a new instance of {@link LocalizedPropertyValue}.
	 *
	 * @return an instance of the bean specified by {@link #setLocalizedPropertyValueBeanId(String)}
	 */
	protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
		return getBean(localizedPropertyValueBean);
	}

	@Override
	public Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	/**
	 * Returns the localized property bean ID.
	 *
	 * @return the localized property bean ID.
	 */
	protected String getLocalizedPropertyValueBean() {
		return localizedPropertyValueBean;
	}

	@Override
	public void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> map, final String localizedPropertyValueBean) {
		if (map != null) {
			localizedPropertiesMap = map;
			this.localizedPropertyValueBean = localizedPropertyValueBean;
		}
	}

	/**
	 * Gets the value of the given property for the given locale.
	 * This implementation looks up the property in a map using the full locale string
	 * as a key (language_country). If it doesn't find anything, it tries again using
	 * just the language as a key. If still nothing, returns <code>null</code>.
	 *
	 * @param propertyName the property name
	 * @param locale the locale
	 * @return the value of the given property and locale, or null if it doesn't exist.
	 */
	@Override
	public String getValueWithoutFallBack(final String propertyName, final Locale locale) {
		LocalizedPropertyValue value = null;

		// Try with the entire locale string as a key first (language_country)
		value = localizedPropertiesMap.get(propertyName + SEPARATOR + locale.toString());

		// If null, try with just the language as a key
		if (value == null) {
			value = localizedPropertiesMap.get(propertyName + SEPARATOR + locale.getLanguage());
		}
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LocalizedPropertiesImpl)) {
			return false;
		}

		final LocalizedPropertiesImpl other = (LocalizedPropertiesImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(localizedPropertiesMap, other.localizedPropertiesMap)
			.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(localizedPropertiesMap)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("localizedPropertiesMap", getLocalizedPropertiesMap())
			.toString();
	}
}
