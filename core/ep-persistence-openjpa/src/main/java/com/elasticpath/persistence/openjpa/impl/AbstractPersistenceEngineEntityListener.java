package com.elasticpath.persistence.openjpa.impl;

import org.apache.openjpa.event.LifecycleEvent;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.openjpa.PersistenceEngineEntityListener;

/**
 * Abstract implementation of {@code AbstractPersistenceEngineEntityListener}.
 */
public class AbstractPersistenceEngineEntityListener implements PersistenceEngineEntityListener {

	@Override
	public void beginBulkOperation(final String queryName, final String queryString, final String parameters,
			final ChangeType type) {
		// No - op
	}

	@Override
	public void beginSingleOperation(final Persistable object, final ChangeType type) {
		// No - op
	}

	@Override
	public void endBulkOperation() {
		// No - op
	}

	@Override
	public void endSingleOperation(final Persistable object, final ChangeType type) {
		// No - op
	}

	@Override
	public void eventOccurred(final LifecycleEvent event) {
		// No - op
	}
}
