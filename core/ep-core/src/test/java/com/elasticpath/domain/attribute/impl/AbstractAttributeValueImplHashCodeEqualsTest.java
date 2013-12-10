/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.attribute.impl;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Associated class to handle common {@link AbstractAttributeValueImpl} set up.
 */

public class AbstractAttributeValueImplHashCodeEqualsTest {

	private static final Integer INTEGER_VALUE = Integer.valueOf(10);

	private static final BigDecimal DECIMAL_VALUE = BigDecimal.TEN;

	private static final Date DATE_VALUE = new Date(123456);

	private static final String ATTRIBUTE_KEY = "KEY";

	private static final String LANGUAGE_CODE = "CA_en";

	private static final String LOCALIZED_ATTRIBUTE_KEY = ATTRIBUTE_KEY   + "_" +  LANGUAGE_CODE;

	private static final String SHORT_TEXT_VALUE = "SHORT_TEXT_VALUE";

	private static final String LONG_TEXT_VALUE = "LONG_TEXT_VALUE";

	private static final int ATTRIBUTE_TYPE_ID = 1234;


	/**
	 * Populate abstract attribute value.
	 *
	 * @param abstractAttributeValue the abstract attribute value
	 */
	protected void populateAbstractAttributeValue(final AbstractAttributeValueImpl abstractAttributeValue) {
		abstractAttributeValue.setIntegerValue(INTEGER_VALUE);
		abstractAttributeValue.setDecimalValue(DECIMAL_VALUE);
		abstractAttributeValue.setBooleanValue(false);
		abstractAttributeValue.setDateValue(DATE_VALUE);
		abstractAttributeValue.setAttribute(createAttribute());
		abstractAttributeValue.setShortTextValue(SHORT_TEXT_VALUE);
		abstractAttributeValue.setLongTextValue(LONG_TEXT_VALUE);
		abstractAttributeValue.setLocalizedAttributeKey(LOCALIZED_ATTRIBUTE_KEY);
		abstractAttributeValue.setAttributeTypeId(ATTRIBUTE_TYPE_ID);
	}

	/**
	 * Creates the attribute.
	 *
	 * @return the attribute impl
	 */
	protected AttributeImpl createAttribute() {
		AttributeImpl result = new AttributeImpl();
		result.setKey(ATTRIBUTE_KEY);
		return result;
	}
}