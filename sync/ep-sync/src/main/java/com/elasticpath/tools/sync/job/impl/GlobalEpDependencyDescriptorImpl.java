package com.elasticpath.tools.sync.job.impl;

import java.util.Map;

import com.elasticpath.tools.sync.job.GlobalEpDependencyDescriptor;

/**
 * Determines dependency order for all EP domain classes.
 */
public class GlobalEpDependencyDescriptorImpl implements GlobalEpDependencyDescriptor {

	private static final int DEFAULT_VALUE = 0;

	private Map<Class<?>, Integer> domainClassOrdering;

	@Override
	public int getPlace(final Class< ? > businessObjectType) {
		Integer result = domainClassOrdering.get(businessObjectType);
		if (result != null) {
			return result;
		}
		return DEFAULT_VALUE;
	}

	/**
	 * Sets the ordering of domain classes. Lower integer values represent a higher priority and closer ordering to the
	 * front. Accepts negatives.
	 * 
	 * @param domainClassOrdering ordering of domain classes
	 */
	public void setDomainClassOrdering(final Map<Class<?>, Integer> domainClassOrdering) {
		this.domainClassOrdering = domainClassOrdering;
	}
}
