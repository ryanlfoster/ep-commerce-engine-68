package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.target.JobTransactionCallback;

/**
 * No-op implementation of JobTransactionCallback. Subclasses which do not require all 
 * methods can inherit from this class. 
 */
public abstract class AbstractJobTransactionCallbackAdapter implements JobTransactionCallback {

	@Override
	public void preUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		//No-op implementation
	}

	@Override
	public void postUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		//No-op implementation
	}

	@Override
	public void preRemoveJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		//No-op implementation
	}

	@Override
	public void postRemoveJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		//No-op implementation
	}

	@Override
	public abstract String getCallbackID();
}
