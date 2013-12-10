package com.elasticpath.tools.sync.job;

/**
 * Determines dependency order for all EP domain classes.
 */
public interface GlobalEpDependencyDescriptor {

	/**
	 * Gets place of the given class in the list of dependent business object types. Object types that are not known
	 * should return a default place (generally 0). The place is therefore not unique.
	 * 
	 * @param businessObjectType class of domain object
	 * @return positive integer number
	 */
	int getPlace(final Class< ? > businessObjectType);
}
