package com.elasticpath.domain.contentspace;


import com.elasticpath.persistence.api.Entity;

/**
 * Locale dependant value for parameter value.
 */
public interface ParameterLocaleDependantValue extends Entity {
	
	/**
	 * Sets the locale. Null value is acceptable.
	 *
	 * @param locale the locale to set.
	 */
	void setLocale(String locale);

	/**
	 * Returns the locale. Null value is possible for non localized parameters.
	 *
	 * @return the locale. 
	 */
	String getLocale();
	
	/**
	 * Set the value. 
	 * @param value the value to set.
	 */
	void setValue(String value);
	
	/**
	 * Get the value.
	 * @return the value
	 */
	String getValue();


}
