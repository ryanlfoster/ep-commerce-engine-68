/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.validation.constraints.LongTextValueSize;
import com.elasticpath.validation.constraints.ShortTextMultiValuesElementSize;
import com.elasticpath.validation.constraints.ShortTextValueSize;


/**
 * <code>AttributeValueWithType</code> is an association domain model which
 * contains an <code>Attribute</code> with a value.  Unlike <code>AttributeValue</code>,
 * it also provide ways to access a typed value.
 */
@ShortTextValueSize(max = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
@LongTextValueSize(max = GlobalConstants.LONG_TEXT_MAX_LENGTH)
@ShortTextMultiValuesElementSize(max = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
public interface AttributeValueWithType extends AttributeValue, Comparable<AttributeValueWithType> {

	/**
	 * Get the short text value.
	 *
	 * @return the short text value
	 */
	String getShortTextValue();

	/**
	 * Set the short text value.
	 *
	 * @param shortTextValue the short text value
	 */
	void setShortTextValue(final String shortTextValue);

	/**
	 * Get the long text value.
	 *
	 * @return the long text value
	 */
	String getLongTextValue();

	/**
	 * Set the long text value.
	 *
	 * @param longTextValue the long text value
	 */
	void setLongTextValue(final String longTextValue);

	/**
	 * Get the integer value.
	 *
	 * @return the integer value
	 */
	Integer getIntegerValue();

	/**
	 * Set the integer value.
	 *
	 * @param integerValue the integer value
	 */
	void setIntegerValue(final Integer integerValue);

	/**
	 * Get the decimal value.
	 * @return the decimal value
	 */
	BigDecimal getDecimalValue();

	/**
	 * Set the decimal value.
	 * @param decimalValue the decimal value
	 */
	void setDecimalValue(final BigDecimal decimalValue);

	/**
	 * Get the boolean value.
	 * @return the boolean value
	 */
	Boolean getBooleanValue();

	/**
	 * Set the boolean value.
	 * @param booleanValue the boolean value
	 */
	void setBooleanValue(final Boolean booleanValue);

	/**
	 * Get the date value.
	 * @return the date value
	 */
	Date getDateValue();

	/**
	 * Set the date value.
	 * @param dateValue the date value
	 */
	void setDateValue(final Date dateValue);

	/**
	 * Get the multi-values for short text.
	 * @return the shortTextMultiValues
	 */
	List<String> getShortTextMultiValues();

	/**
	 * Set the multi-values for short text.
	 * @param shortTextMultiValues the shortTextMultiValues to set
	 */
	void setShortTextMultiValues(final List<String> shortTextMultiValues);

}