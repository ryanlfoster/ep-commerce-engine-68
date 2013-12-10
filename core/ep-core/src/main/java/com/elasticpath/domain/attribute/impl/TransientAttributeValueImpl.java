/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.attribute.impl;

import javax.persistence.Transient;

/**
 * Implementation of <code>AttributeValue</code> for transient attribute values.
 */
public class TransientAttributeValueImpl extends AbstractAttributeValueImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Get the UidPk which should always be 0 to indicate transience.
	 * @return 0 to indicate non-persisted value.
	 */
	@Transient
	public long getUidPk() {
		return 0;
	}

	/**
	 * This setter should not be used for a transient attribute value.
	 * @param uidPk the uidPk to ignore
	 */
	public void setUidPk(final long uidPk) {
		// Do nothing.
	}

}
