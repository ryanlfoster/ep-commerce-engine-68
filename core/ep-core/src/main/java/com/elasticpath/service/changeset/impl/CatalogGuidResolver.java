/**
 * 
 */
package com.elasticpath.service.changeset.impl;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * Catalog guid resolver class.
 *
 */
public class CatalogGuidResolver implements ObjectGuidResolver {

	@Override
	public String resolveGuid(final Object object) {
		Catalog catalog = (Catalog) object;
		return catalog.getCode();
	}

	@Override
	public boolean isSupportedObject(final Object object) {
		if (object instanceof Catalog) {
			Catalog catalog = (Catalog) object;
			return !catalog.isMaster();
		}
		return false;
	}

}
