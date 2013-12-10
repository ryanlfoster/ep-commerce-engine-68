/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.persistence.api;

import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;

/**
* Provides standard implementations of methods required for implementations of {@link Entity}.
*/
public final class EntityUtils {
	private EntityUtils() {
	}

	/**
	 * Calculates the hashCode of an entity, suitable for returning from an implementation of {@link Object#hashCode()}.
	 * @param entity the entity
	 * @return the hashCode of the provided entity
	 */
	public static int hashCode(final Entity entity) {
		return ObjectUtils.hashCode(entity.getGuid());
	}

	/**
	 * Calculates the equality of an entity and some other object. This can be used as an implementation of {@link Object#equals(Object)}.
	 * @param entity the entity
	 * @param other another object
	 * @return true if the entity and other object are equal, false otherwise
	 */
	@SuppressWarnings({"PMD.SuspiciousEqualsMethodName", "PMD.CompareObjectsWithEquals"})
	public static boolean equals(final Entity entity, final Object other) {
		if (entity == other) {
			return true;
		}

		if (!(other instanceof AbstractEntityImpl)) {
			return false;
		}

		AbstractEntityImpl otherEntity = (AbstractEntityImpl) other;
		return ObjectUtils.equals(entity.getGuid(), otherEntity.getGuid());
	}

	/**
	 * Initializes the guid of an entity.
	 * @param entity the entity
	 */
	public static void initializeGuid(final Entity entity) {
		if (entity.getGuid() == null) {
			entity.setGuid(UUID.randomUUID().toString());
		}
	}
}
