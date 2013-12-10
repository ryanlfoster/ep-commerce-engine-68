package com.elasticpath.search.index.solr.service;

/**
 * Factory for creating {@code IndexBuildPolicyContext}. This uses Spring's ServiceLocator. Use this instead of creating the bean by String name.
 */
public interface IndexBuildPolicyContextFactory {

	/**
	 * Create a new {@code IndexBuildPolicyContext}.
	 * 
	 * @return a new {@code IndexBuildPolicyContext}.
	 */
	IndexBuildPolicyContext createIndexBuildPolicyContext();
}
