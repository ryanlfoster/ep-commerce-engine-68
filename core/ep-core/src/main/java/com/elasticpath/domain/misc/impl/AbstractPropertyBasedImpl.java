package com.elasticpath.domain.misc.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.PropertyBased;

/**
 * Abstract implementation of a transient property based object.
 * 
 * @deprecated use {@link com.elasticpath.persistence.dao.impl.AbstractPropertyLoaderAwareImpl}
 */
@Deprecated
public class AbstractPropertyBasedImpl extends AbstractEpDomainImpl implements PropertyBased {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/** The property name. */
	private String propertyName;

	/** Main map of all properties. */
	private Map<String , ? extends Map<?, ?>> propertiesMap;

	/**
	 * Set the properties files that are used as a source of a data.
	 *
	 * @param propertiesMap the map containing properties data.
	 */
	public void setPropertiesMap(final Map<String, ? extends Map<Object, Object>> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

	/**
	 * Get property name.
	 *
	 * @return property name
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Set property name.
	 *
	 * @param propertyName the property name
	 */
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Returns a map of properties.
	 *
	 * @param propertyName the propertyName
	 * @return the map of properties
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, String> getPropertyMap(final String propertyName) {
		sanityCheck();
		return (Map<String, String>) propertiesMap.get(propertyName);
	}

	/**
	 * Returns a map of properties.
	 *
	 * @param propertyName the propertyName
	 * @param locale the current locale to load
	 * @return the map of properties
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, String> getPropertyMap(final String propertyName, final Locale locale) {
		sanityCheck();

		Map<String, String> propertyMap = null;

		if (locale != null) {
			final String suffix = getLocaleSuffix(locale);
			final String propertyFileName = propertyName + suffix;
			propertyMap = (Map<String, String>) propertiesMap.get(propertyFileName);
		}

		if (propertyMap == null) {
			propertyMap = getPropertyMap(propertyName);
		}

		return propertyMap;
	}

	/**
	 * Checks that the required objects have been set.
	 */
	protected void sanityCheck() {
		if (propertiesMap == null) {
			throw new EpDomainException("The properties Map has not been set.");
		}
	}

	/**
	 * Returns current locale as a string preceed by an underscore. EX: _fr or _fr_CA
	 *
	 * @param locale the selected locale
	 * @return locale suffix
	 */
	protected String getLocaleSuffix(final Locale locale) {
		if (locale == null) {
			return "";
		}
		return "_" + locale.toString();
	}

}
