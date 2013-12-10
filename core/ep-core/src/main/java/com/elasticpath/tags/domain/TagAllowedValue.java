package com.elasticpath.tags.domain;



import java.util.Locale;

import com.elasticpath.domain.misc.Orderable;
import com.elasticpath.persistence.api.Persistable;

/**
 * Interface for allowed values in 'Tag Definition's.
 */
public interface TagAllowedValue extends Persistable, Orderable {
	
	/**
	 * The name of localized property -- display name.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "tagAllowedValueDescription";
	
	
    /**
     * 
     * Value a regular expression rule.
     * 
     * @return the value of 'Tag Definition'
     */
    String getValue();

    /**
     * Sets a value to 'Tag Definition'.
     *
     * @param value a value to be set
     */
    void setValue(String value);
    
    /**
     * 
     * Localized description explain the allowed value.
     * 
     * @param locale the required locale
     * @return allowed value name in language requested 
     *         or default description if no localized value present
     */
	String getLocalizedDescription(Locale locale);    
    
    /**
     * 
     * Description explain the allowed value.  
     * 
     * @return the description of 'Allowed value'
     */
    String getDescription();
    
    /**
     * 
     * Set the description of allowed value.
     * 
     * @param description a description to set.
     */
	void setDescription(String description);
	
	
	/**
	 * Get the order in which this allowed value should appear.
	 * 
	 * @return the ordering
	 */
	int getOrdering();

	/**
	 * Set the order in which this allowed value should appear.
	 * 
	 * @param ordering the ordering
	 */
	void setOrdering(int ordering);
	
}
