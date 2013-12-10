/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.changeset;

/**
 * Inteface for object guid resolver.
 */
public interface ObjectGuidResolver {

	/**
	 * Resolve the guid.
	 * 
	 * @param object the object which guid is got from.
	 * @return guid 
	 */
	String resolveGuid(Object object);

	/**
	 * Is the object a supported object for this resolver.
	 * 
	 * @param object the object 
	 * @return true if this resolver supports the object
	 */
	boolean isSupportedObject(Object object);

}