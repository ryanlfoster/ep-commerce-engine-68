package com.elasticpath.domain.attribute.impl;

import com.elasticpath.domain.attribute.AttributeValue;

/**
 * Arbitrary faux extension class for testing purposes.
 */
public class ExtAttributeValueTestImpl extends AbstractAttributeValueImpl implements AttributeValue {
	private static final long serialVersionUID = 9113976178894340257L;

	/**
	 * Gets the unique identifier for this domain object. This unique identifier is system-dependent. That means on different systems(like staging
	 * and production environments), different identifiers might be assigned to the same(from business perspective) domain object.
	 * <p/>
	 * Notice: not all persistent domain objects has unique identifier. Some value objects don't have unique identifier. They are cascade loaded and
	 * updated through their parents.
	 *
	 * @return the unique identifier.
	 */
	@Override
	public long getUidPk() {
		return 0;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		// Happy now PMD?!
	}
}
