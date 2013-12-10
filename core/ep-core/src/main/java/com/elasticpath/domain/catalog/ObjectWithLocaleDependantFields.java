package com.elasticpath.domain.catalog;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.localization.LocaleFallbackPolicy;



/**
 * Represents an object with <code>LocaleDependantFields</code>.
 */
public interface ObjectWithLocaleDependantFields {

	/**
	 * Sets the all <code>LocaleDependantFields</code> with the given map.
	 *
	 * @param localeDependantFieldsMap the map to set
	 */
	void setLocaleDependantFieldsMap(Map<Locale, LocaleDependantFields> localeDependantFieldsMap);

	/**
	 * Returns the {@link LocaleDependantFields} of the given locale (cannot be <code>null</code>).
	 * Falls back to the language and country (if variant present) then to the language (if
	 * country is present). If no field is defined, returns a fallback
	 * {@link LocaleDependantFields}.
	 *
	 * @param locale the locale
	 * @return the {@link LocaleDependantFields} of the given non-null locale if it exists,
	 *         otherwise an empty {@link LocaleDependantFields}
	 */
	LocaleDependantFields getLocaleDependantFields(Locale locale);

	/**
	 * Returns the <code>LocaleDependantFields</code> of the given locale without fallback values.
	 *
	 * @param locale the locale
	 * @return the <code>LocaleDependantFields</code> instance of the given locale if it can be found, otherwise a new
	 *         <code>LocaleDependantFields</code> instance whose localse will be set to the given locale
	 */
	LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(Locale locale);

	/**
	 * Adds or updates the given <code>LocaleDependantFields</code>.
	 *
	 * @param ldf the <code>LocaleDependantFields</code> instance to set.
	 */
	void addOrUpdateLocaleDependantFields(LocaleDependantFields ldf);


	/**
	 * Returns the object's display name formatted using the given locale. 
	 * Falls back to retrieving the display name in the object container's 
	 * default locale if no value is found for the given locale.
	 *
	 * @param locale the locale for which to retrieve the display name
	 * @return the display name
	 */
	String getDisplayName(Locale locale);

	/**
	 * Sets the display name for the object.
	 *
	 * @param name the name
	 * @param locale the locale this name is valid for
	 */
	void setDisplayName(String name, Locale locale);
	
	/**
	 * Returns the LocaleDependantFields of the primary locale in the policy (cannot be null) and
	 * falls back according to policy.
	 *
	 * @param policy fallback policy for locales
	 * @return LocaleDependantField which matches the first locale in the policy
	 */
	LocaleDependantFields getLocaleDependantFields(LocaleFallbackPolicy policy);
}
