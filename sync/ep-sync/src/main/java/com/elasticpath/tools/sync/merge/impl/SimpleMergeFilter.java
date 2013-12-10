package com.elasticpath.tools.sync.merge.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.elasticpath.tools.sync.merge.MergeFilter;

/**
 * Implements simple filter operations for merging objects.
 */
public class SimpleMergeFilter implements MergeFilter {
	
	private boolean mergeAll = true;
	
	private Map<Class < ? >, Set<String>> includeMehods = Collections.emptyMap();
	
	private Map<Class < ? >, Set<String>> excludeMethods = Collections.emptyMap();

	@Override
	public boolean isMergePermitted(final Class< ? > clazz, final String methodName) {
		if (mergeAll) {
			return !getMethodsSet(clazz, excludeMethods).contains(methodName);
		}
		return getMethodsSet(clazz, includeMehods).contains(methodName);
	}

	/**
	 * Gets set of methods by given class.
	 * 
	 * @param clazz the class
	 * @param methods the appropriate methods
	 * @return set of method belongs to given class
	 */
	Set<String> getMethodsSet(final Class< ? > clazz, final Map<Class < ? >, Set<String>> methods) {
		Class< ? > configurationClass = getConfigurationClass(clazz, methods.keySet());
		
		Set<String> methodsSet = methods.get(configurationClass);
		if (methodsSet == null) {
			methodsSet = Collections.emptySet();
		}
		return methodsSet;
	}

	/**
	 * Gets class from configuration that equals to given class or is a subclass of given class. 
	 * 
	 * @param clazz the class
	 * @param configClasses the set of config classes
	 * @return corresponding configuration files or null if given class does not exist in configuration
	 */
	Class< ? > getConfigurationClass(final Class< ? > clazz, final Set<Class< ? >> configClasses) {
		for (Class< ? > clazzKey : configClasses) {
			if (containsInHierarchy(clazz, clazzKey)) {
				return clazzKey;
			}
		}
		return null;
	}
	
	/**
	 * Checks if the given classToCompare equals to <code>configClass</code> or is a super class of <code>configClass</code>.
	 * 
	 * @param classToCompare the classToCompare
	 * @param configClass the configuration class
	 * @return true if classToCompare equal or subclass of configClass and false otherwise
	 */
	boolean containsInHierarchy(final Class< ? > classToCompare, final Class< ? > configClass) {
		return classToCompare.isAssignableFrom(configClass);
	}

	/**
	 * @param mergeAll the mergeAll to set
	 */
	public void setMergeAll(final boolean mergeAll) {
		this.mergeAll = mergeAll;
	}

	/**
	 * @param includeMehods the includeMehods to set
	 */
	public void setIncludeMehods(final Map<Class< ? >, Set<String>> includeMehods) {
		this.includeMehods = includeMehods;
	}

	/**
	 * @param excludeMethods the excludeMethods to set
	 */
	public void setExcludeMethods(final Map<Class< ? >, Set<String>> excludeMethods) {
		this.excludeMethods = excludeMethods;
	}
}
