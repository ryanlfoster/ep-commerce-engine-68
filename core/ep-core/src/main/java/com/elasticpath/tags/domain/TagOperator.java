package com.elasticpath.tags.domain;

import java.util.Locale;

import com.elasticpath.persistence.api.Persistable;

/**
 * TagOperator.
 */
public interface TagOperator extends Persistable  {

	/**
	 * The name of localized property -- display name.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "tagOperatorDisplayName";
	
	/**
     * @return the GUID of the price list
     */
    String getGuid();
    
    /**
     * Get localized name or GUID if no name is mapped for provided locale.
     * @param locale locale
     * @return localized name
     */
    String getName(Locale locale);
}
