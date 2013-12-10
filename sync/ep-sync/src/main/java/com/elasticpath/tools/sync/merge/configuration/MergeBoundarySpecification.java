package com.elasticpath.tools.sync.merge.configuration;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Interface for boundaring merge.
 */
public interface MergeBoundarySpecification {

	/**
	 * Initializes the stopper with baseClass in order to determine the merge boundary for this class.
	 * 
	 * @param baseClazz the base class that will be used as bas fore <code>stopMerging</code> method
	 * @throws SyncToolConfigurationException on case if boundary specification is not set for the class
	 */
	void initialize(final Class< ? > baseClazz)
			throws SyncToolConfigurationException;

	/**
	 * Notifies whether merge should be proceeded recursively for the given object. If <code>initialize</code> method was not called before this
	 * method then it will return false for any class argument.
	 * 
	 * @param clazz class of the given object
	 * @return true if recursive merge should be stopped
	 */
	boolean stopMerging(final Class< ? > clazz);

}