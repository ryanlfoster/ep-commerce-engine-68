package com.elasticpath.service.search.query;

import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Represents different types of sorts within a search.
 */
public interface SortBy extends ExtensibleEnum {

    /**
	 * Returns the string representation of the sorting type.
	 *
	 * @return the string representation of the sorting type
	 */
	String getSortString();
	
}
