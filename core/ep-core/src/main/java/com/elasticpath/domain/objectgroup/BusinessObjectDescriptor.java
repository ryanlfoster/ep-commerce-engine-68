/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup;

import java.io.Serializable;

/**
 * A descriptor for a business object in the system.
 */
public interface BusinessObjectDescriptor extends Serializable {

	/**
	 * Gets the object type.
	 * 
	 * @return the object type to use
	 */
	String getObjectType();
	
	/**
	 * Gets the object identifier.
	 * 
	 * @return the object identifier
	 */
	String getObjectIdentifier();

	/**
	 *
	 * @param objectIdentifier the objectIdentifier to set
	 */
	void setObjectIdentifier(String objectIdentifier);

	/**
	 *
	 * @param objectType the objectType to set
	 */
	void setObjectType(String objectType);
}
