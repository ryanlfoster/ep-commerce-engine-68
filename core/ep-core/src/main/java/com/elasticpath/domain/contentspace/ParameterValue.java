package com.elasticpath.domain.contentspace;

import java.util.Map;

import com.elasticpath.persistence.api.Entity;

/**
 * ParameterValue Represents localized value for parameter.
 */
public interface ParameterValue extends Entity {
	
	/**
	 * Set the parameter. 
	 * @param parameter Parameter instance to set.
	 */
	void setParameter(Parameter parameter);
	
	/**
	 * Get the parameter.
	 * @return Parameter.
	 */
	Parameter getParameter();
	
	/**
	 * Set the value for <code>Parameter</code>. 
	 * @param value String value.
	 * @param language - lower case two-letter ISO-639 code. Can be null, if parameter not support localization.
	 */
	void setValue(String value, String language);
	
	/**
	 * Retrieve the value for given <code>language</code>.  
	 * @param language lower case two-letter ISO-639 code. Can be null, if parameter not support localization.
	 * @return Value.
	 */
	String getValue(String language);
	
	/**
	 * Retrieve a map of all the values and languages for this parameter value.
	 * @return map.
	 */
	Map<String, ParameterLocaleDependantValue> getValues();
	
	/**
	 * Get the parameter name.
	 * @return name of parameter.
	 */
	String getParameterName();

	/**
	 * Set the parameter name.
	 * @param parameterName name of parameter.
	 */	
	void setParameterName(String parameterName);
	
	/**
	 * Set the localizable flag.
	 * @param localizable flag
	 */
	void setLocalizable(boolean localizable);
	
	

	/**
	 * Get localizable flag.
	 * @return true if parameter localizable
	 */
	boolean isLocalizable();
	
	
	/**
	 * Parameter description. 
	 * @return Description of parameter.
	 */
	String getDescription();

	/**
	 * Set description.
	 *
	 * @param description of the parameter 
	 */
	void setDescription(String description);
}
