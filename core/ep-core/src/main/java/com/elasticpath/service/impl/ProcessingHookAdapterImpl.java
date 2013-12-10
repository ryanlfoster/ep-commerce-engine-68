package com.elasticpath.service.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.ProcessingHook;

/**
 * This adaptor class provides default implementations for the methods described by the
 * {@link ProcessingHook} interface.
 * <p>
 * Classes that wish to add a processing hook to a service can extend this class and override only
 * the methods which they are interested in.
 * </p>
 */
public class ProcessingHookAdapterImpl implements ProcessingHook {

	/**
	 * {@inheritDoc}
	 * This default implementation does nothing.
	 */
	public void postAdd(final Persistable domain) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * This default implementation does nothing.
	 */
	public void postDelete(final Persistable object) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * This default implementation does nothing.
	 */
	public void postUpdate(final Persistable oldObject, final Persistable newObject) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * This default implementation does nothing.
	 */
	public void preAdd(final Persistable domain) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * This default implementation does nothing.
	 */
	public void preDelete(final Persistable object) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * This default implementation does nothing.
	 */
	public void preUpdate(final Persistable oldObject, final Persistable newObject) {
		// do nothing
	}
}
