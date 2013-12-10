/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.changeset.impl;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * The default object guid resolver class.
 */
public class DefaultObjectGuidResolver implements ObjectGuidResolver {
	
	private static final Logger LOG = Logger.getLogger(DefaultObjectGuidResolver.class);

	@Override
	public String resolveGuid(final Object object) {
		if (!(object instanceof Entity)) {
			LOG.error("Business object must be of type: " + Entity.class.getName());
				
			return null;
		}
			
		return ((Entity) object).getGuid();
	}

	/**
	 * This is the default object guid resolver which returns true for any object.
	 * 
	 * @param object the object
	 * @return always return true
	 */
	public boolean isSupportedObject(final Object object) {
		return true;
	}
}
