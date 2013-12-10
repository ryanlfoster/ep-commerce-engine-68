/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.persistence.api;

import java.io.Serializable;

/**
 * Represents objects that can be persisted.
 */
public interface Persistable extends Serializable {
	/**
	 * Gets the unique identifier for this domain object. This unique identifier is system-dependent. That means on different systems(like staging
	 * and production environments), different identifiers might be assigned to the same(from business perspective) domain object.
	 * <p>
	 * Notice: not all persistent domain objects has unique identifier. Some value objects don't have unique identifier. They are cascade loaded and
	 * updated through their parents.
	 * 
	 * @return the unique identifier.
	 */
	long getUidPk();

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	void setUidPk(final long uidPk);

	/**
	 * <code>true</code> if the object has previously been persisted.
	 * <p>
	 * Notice: not all persistent domain objects has unique identifier. Some value objects don't have unique identifier. They are cascade loaded and
	 * updated through their parents. It doesn't make sense to call this method on those value object.
	 * 
	 * @return <code>true</code> if the object has previously been persisted.
	 */
	boolean isPersisted();
}
