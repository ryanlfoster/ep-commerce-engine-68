/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.attribute;

import java.util.Date;

/**
 * <code>CustomerProfileValue</code> represents an <code>Attribute</code> value for a <code>Customer</code>.
 */
public interface CustomerProfileValue extends AttributeValueWithType {

	/**
	 * Get the date that this was last modified on.
	 * 
	 * @return the last modified date
	 */
	Date getLastModifiedDate();
	
}
