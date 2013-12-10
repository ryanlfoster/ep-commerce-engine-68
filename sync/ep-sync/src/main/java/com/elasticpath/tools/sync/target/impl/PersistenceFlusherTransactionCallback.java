package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tools.sync.job.JobEntry;


/**
 * Persistable Flusher Callback.  
 * Calls {@link PersistenceEngine#flush()} whenever the object being processed changes.
 */
public class PersistenceFlusherTransactionCallback extends AbstractJobTransactionCallbackAdapter {

	private final ThreadLocal<Class<?>> objectType = new ThreadLocal<Class<?>>();

	private PersistenceEngine persistenceEngine;

    @Override
    public String getCallbackID() {
        return "Persistable Flusher Callback";
    }

    @Override
    public void preUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
        jobEntryChanged(targetPersistence);
    }

    @Override
    public void preRemoveJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
        jobEntryChanged(targetPersistence);
    }

    private void jobEntryChanged(final Persistable targetPersistence) {
        if (targetPersistence == null) {
            objectType.set(null);
            return;
        }
        
        final Class<?> classOfTarget = targetPersistence.getClass();

        if (classOfTarget.equals(objectType.get())) {
            return; //already set, don't need to do anything.
        }
        
        objectType.set(classOfTarget);
        
        persistenceEngine.flush();
    }

    /**
     * Sets the {@link PersistenceEngine}.
     *
     * @param persistenceEngine the persistenceEngine to set
     */
    public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
        this.persistenceEngine = persistenceEngine;
    }
}
