/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;

/**
 * This is a proxy class for the <code>StoreResourceManagerImpl</code> singleton. Velocity does not allow one to specify an existing instance for
 * the resource manager, only the class name (from which it creates a new instance). In order to inject some of our beans into the store resource
 * manager, we need to have this proxy in place to delegate to our store resource manager singleton.
 */
public class StoreResourceManagerProxyImpl extends ResourceManagerImpl {

	private final ResourceManager delegate = StoreResourceManagerImpl.getInstance();

	/**
	 * {@inheritDoc}
	 * 
	 * @see StoreResourceManagerImpl
	 */
	@Override
	public Resource getResource(final String resourceName, final int resourceType, final String encoding) throws Exception {
		return delegate.getResource(resourceName, resourceType, encoding);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see StoreResourceManagerImpl
	 */
	@Override
	public String getLoaderNameForResource(final String resourceName) {
		return delegate.getLoaderNameForResource(resourceName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see StoreResourceManagerImpl
	 */
	@Override
	@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
	public synchronized void initialize(final RuntimeServices rsvc) throws Exception {
		delegate.initialize(rsvc);
	}

}
