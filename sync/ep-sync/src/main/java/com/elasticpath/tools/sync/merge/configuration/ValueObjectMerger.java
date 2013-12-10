package com.elasticpath.tools.sync.merge.configuration;

/**
 * Merges configured value objects which have state fields. It serves exceptional situations when
 * two value objects are equal according to implementation (equals method) but not equal
 * from business perspective. Usually it is applied for different associations.
 */
public interface ValueObjectMerger {

	/**
	 * Checks if merge procedure is required for objects of the given class.
	 * 
	 * @param clazz class to check necessity of merge for
	 * @return true if it is required to provide custom merge
	 */
	boolean isMergeRequired(final Class< ? > clazz);

	/**
	 * Merges fields specified in configuration.
	 * 
	 * @param source source object
	 * @param target target object to be updated
	 */
	void merge(final Object source, final Object target);
}
