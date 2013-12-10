package com.elasticpath.commons.pagination;

import java.io.Serializable;

/**
 * Represents the value of a field to use to constrain the search.
 */
public class SearchCriterion implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000003L;
	
	private final String fieldName;
	private final String fieldValue;

	/**
	 * Default ctor.
	 * @param fieldName The name of the criterion field.
	 * @param fieldValue The value
	 */
	public SearchCriterion(final String fieldName, final String fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	/**
	 * 
	 * @return The name of the field.
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	/**
	 * 
	 * @return The value of the field
	 */
	public String getFieldValue() {
		return fieldValue;
	}
}
