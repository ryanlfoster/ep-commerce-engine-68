package com.elasticpath.commons.util.security;

/**
 * A string mask utility. Allow to hide a security sensitive information from exposing.
 *  
 * @param <T> a type of masked object. In most cases it's a string.
 */
public interface MaskUtility<T> {

	/**
	 * Hide all sensitive fields/objects or replace to non-sensitive char(s).
	 * It's depends on implementation. 
	 *  
	 * @param objectToMask - the object to processing masking.
	 * @return - the masked object.
	 */
	T mask(final T objectToMask);

}