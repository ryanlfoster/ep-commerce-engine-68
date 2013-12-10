/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */

package com.elasticpath.commons.constants;

/**
 * This interface contains type numeric constants.
 * 
 */

public interface IType {

	/**
	 * Attribute type id for short text.
	 */
	int SHORT_TEXT_TYPE_ID = 1;
	
	/**
	 * Attribute type id for short text multivalue.
	 */
	int SHORT_TEXT_MULTI_VALUE_TYPE_ID = 6;

	/**
	 * Attribute type id for long text.
	 */
	int LONG_TEXT_TYPE_ID = 2;

	/**
	 * Attribute type id for integer.
	 */
	int INTEGER_TYPE_ID = 3;

	/**
	 * Attribute type id for decimal.
	 */
	int DECIMAL_TYPE_ID = 4;

	/**
	 * Attribute type id for boolean.
	 */
	int BOOLEAN_TYPE_ID = 5;

	/**
	 * Attribute type id for image.
	 */
	int IMAGE_TYPE_ID = 7;

	/**
	 * Attribute type id for file.
	 */
	int FILE_TYPE_ID = 8;

	/**
	 * Attribute type id for data.
	 */
	int DATE_TYPE_ID = 9;

	/**
	 * Attribute type id for data time.
	 */
	int DATETIME_TYPE_ID = 10;

	/**
	 * Type id for URL.
	 */
	int URL_TYPE_ID = 11;
	
	/**
	 * Type id for Product.
	 */
	int PRODUCT_TYPE_ID = 12;
	
	/**
	 * Type id for Category.
	 */
	int CATEGORY_TYPE_ID = 13;
	
	/**
	 * Type id for Category.
	 */
	int HTML_TYPE_ID = 14;
	

	 /**
     * Returns the attribute type name.
     * 
     * @return the attribute type name
     */
    String toString();

}