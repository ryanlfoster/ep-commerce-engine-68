/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.search.index.solr.service;

/**
 * An index build policy.
 */
public interface IndexBuildPolicy {

	/**
	 * Gets the value of the optimization flag.
	 * 
	 * @param context the index policy context holding data for the index build status, the index type, etc.
	 * @return true if the index should be optimized
	 */
	boolean isOptimizationRequired(IndexBuildPolicyContext context);
	
	/**
	 * Tests whether a commit is required depending on the parameters of the given context.
	 * 
	 * @param context the index build policy context
	 * @return true if commit has to be performed on the indexes
	 */
	boolean isCommitRequired(IndexBuildPolicyContext context);
	
	/**
	 * Tests whether the accumulated by the index builder documents should be requested for addition.
	 * 
	 * @param context the index build policy context
	 * @return true if the changes done so far should be added to the search index
	 */
	boolean isAddDocumentsRequired(IndexBuildPolicyContext context);
}
