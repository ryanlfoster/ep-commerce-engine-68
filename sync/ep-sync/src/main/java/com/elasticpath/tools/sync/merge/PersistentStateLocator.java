package com.elasticpath.tools.sync.merge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Interface for JPAPersistentStateLocator.
 */
public interface PersistentStateLocator {

	/**
	 * Traverses from the passes class through class hierarchy up to Object collecting basic, single-valued and collection-valued properties, placing
	 * them into separate lists. The algorithm considers possibility for a method to be overridden in which case most specific method will be taken.
	 * 
	 * @param <T> extends Annotation
	 * @param clazz class of an entity
	 * @param basicAttributes a set collecting basic attributes
	 * @param singleValuedAssociations a set collecting single valued associations
	 * @param collectionValuedAssociations a set collecting collection valued associations
	 * @param postLoadMethods a set of methods marked for post load execution
	 * @throws SyncToolRuntimeException if a method to invoke can not be found or some other Reflection errors occurred.
	 */
	<T extends Annotation> void extractPersistentStateAttributes(
			final Class< ? > clazz, final Map<Method, Method> basicAttributes,
			final Map<Method, Method> singleValuedAssociations,
			final Map<Method, Method> collectionValuedAssociations,
			final Set<Method> postLoadMethods)
			throws SyncToolRuntimeException;

}