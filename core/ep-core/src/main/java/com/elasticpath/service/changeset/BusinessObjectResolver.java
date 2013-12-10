package com.elasticpath.service.changeset;

import java.util.Set;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Interface for resolving BusinessObjectDescriptors from objects.
 */
public interface BusinessObjectResolver {

	/**
	 * Resolves the given object to a BusinessObjectDescriptor.
	 * 
	 * @param object The object to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 * @throws ChangeSetPolicyException if the resolution fails (e.g. the object has a null guid). 
	 */
	BusinessObjectDescriptor resolveObjectDescriptor(final Object object) throws ChangeSetPolicyException;
	
	/**
	 * Resolves the given objects to a {@link BusinessObjectDescriptor}.
	 * 
	 * @param objects The set of objects to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 */
	Set<BusinessObjectDescriptor> resolveObjectDescriptor(final Set< ? > objects);

	/**
	 * Gets the class associated with the given object descriptor.
	 * 
	 * @param objectDescriptor the object descriptor. 
	 * 		  if the value is <code>null</code> the result returned will be <code>null</code>.
	 * @return the class instance or null if the class has not been specified
	 */
	Class< ? > getObjectClass(final BusinessObjectDescriptor objectDescriptor);

	/**
	 * Resolves the given object guid.
	 * 
	 * @param object The object to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 */
	String resolveObjectGuid(final Object object);

	/**
	 * Checks whether a class is a supported by change sets class.
	 * @param clazz the class to be checked
	 * @return true if the class is supported by the change set framework
	 */
	boolean isClassSupported(final Class< ? > clazz);

}