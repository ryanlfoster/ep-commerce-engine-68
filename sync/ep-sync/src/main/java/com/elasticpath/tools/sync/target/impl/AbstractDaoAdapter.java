package com.elasticpath.tools.sync.target.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.target.DaoAdapter;

/**
 * Abstract class for {@link DaoAdapter}.
 * 
 * @param <T> Persistable class to synchronize
 */
public abstract class AbstractDaoAdapter<T extends Persistable> implements DaoAdapter<T> {

	/** Default priority for an associated type. */
	public static final int DEFAULT_PRIORITY = 0;

	private EntityLocator entityLocator;

	private Collection<Class<? extends Persistable>> associatedTypePriorities = new ArrayList<Class<? extends Persistable>>();

	/**
	 * @return the entityLocator
	 */
	public EntityLocator getEntityLocator() {
		return entityLocator;
	}

	/**
	 * @param entityLocator the entityLocator to set
	 */
	public void setEntityLocator(final EntityLocator entityLocator) {
		this.entityLocator = entityLocator;
	}

	@Override
	public Collection<Class< ? extends Persistable>> getAssociatedTypes() {
		return associatedTypePriorities;
	}

	/**
	 * Sets the associated types for this adapter.
	 * 
	 * @param associatedTypes associated types
	 * @see #getAssociatedTypes()
	 */
	public void setAssociatedTypes(final Collection<Class<? extends Persistable>> associatedTypes) {
		this.associatedTypePriorities = associatedTypes;
	}
}
