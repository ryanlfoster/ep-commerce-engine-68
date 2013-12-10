package com.elasticpath.sfweb.formbean;

/**
 * A simple container object for holding information on the fromField and toField fields for an attribute range.
 *
 */
public interface NonPreDefinedAttributeRangeFieldFormBean {

	/**
	 * @param fromField the fromField toField set
	 */
	void setFromField(String fromField);

	/**
	 * @return the fromField
	 */
	String getFromField();

	/**
	 * @param toField the toField toField set
	 */
	void setToField(String toField);

	/**
	 * @return the toField
	 */
	String getToField();

}