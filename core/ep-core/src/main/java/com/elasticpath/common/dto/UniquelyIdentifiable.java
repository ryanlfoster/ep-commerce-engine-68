package com.elasticpath.common.dto;

/**
 * Marks an object as having a UidPK.
 */
public interface UniquelyIdentifiable {
	/**
	 * Gets the unique identifier for this domain object. This unique identifier is system-dependant. That means on differenct systems(like staging
	 * and production environments), different identifiers might be assigned to the same(from business perspective) domain object.
	 * <p>
	 * Notice: not all persistent domain objects has unique identifier. Some value objects don't have unique identifer. They are cascading loaded and
	 * updated through their parents.
	 * 
	 * @return the unique identifier.
	 */
	long getUidPk();
}
