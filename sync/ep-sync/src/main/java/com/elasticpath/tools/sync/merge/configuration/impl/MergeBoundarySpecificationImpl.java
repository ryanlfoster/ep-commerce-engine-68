package com.elasticpath.tools.sync.merge.configuration.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.merge.configuration.MergeBoundarySpecification;

/**
 * Provides boundary condition when merge process should be abandoned.
 * Each granular class (e.g. Product, ProductSku, Promotion) should have its own list of stop classes.
 */
public class MergeBoundarySpecificationImpl implements MergeBoundarySpecification {

	private Map<Class< ? >, Set<Class< ? >>> mergeBoundaryMap;

	private Set<Class< ? >> currentBoundary = Collections.emptySet();

	/**
	 * Initializes the stopper with baseClass in order to determine the merge boundary for this class.
	 * 
	 * @param baseClazz the base class that will be used as bas fore <code>stopMerging</code> method
	 * @throws SyncToolConfigurationException on case if boundary specification is not set for the class
	 */
	public void initialize(final Class< ? > baseClazz) throws SyncToolConfigurationException {
		currentBoundary = mergeBoundaryMap.get(baseClazz);
		if (currentBoundary == null) {
			throw new SyncToolConfigurationException("Boundary specification for: " + baseClazz + " could not be found.");			
		}
	}

	/**
	 * Notifies whether merge should be proceeded recursively for the given object. If <code>initialize</code> method was not called before this
	 * method then it will return false for any class argument.
	 * 
	 * @param clazz class of the given object
	 * @return true if recursive merge should be stopped
	 */
	public boolean stopMerging(final Class< ? > clazz) {
		return currentBoundary.contains(clazz);
	}

	/**
	 * @param mergeBoundaryMap the mergeBoundaryMap to set
	 */
	public void setMergeBoundaryMap(final Map<Class< ? >, Set<Class< ? >>> mergeBoundaryMap) {
		this.mergeBoundaryMap = mergeBoundaryMap;
	}
}
