package com.elasticpath.tools.sync.merge;


/**
 * This interface represents merge filter that can be used to add conditions for merge objects.
 */
public interface MergeFilter {
	
	/**
	 * Decides if merge permitted for object corresponding to given method.
	 * 
	 * @param clazz the base class where filter operation processes
	 * @param methodName the method marked with jpa annotation that belongs to given class
	 * @return true if merge permitted and false otherwise
	 */
 	boolean isMergePermitted(final Class< ? > clazz, final String methodName);

}
