package com.elasticpath.domain.dataimport;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a import type.
 */
public interface ImportType extends EpDomain {
	/**
	 * Return the import type Id.
	 *
	 * @return the import type Id
	 */
	int getTypeId();

	/**
	 * Returns the import type name message key.
	 *
	 * @return the import type name message key.
	 */
	String getNameMessageKey();
}