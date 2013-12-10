package com.elasticpath.tools.sync.target.impl;

import java.util.Collection;

import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.JobEntry;


/**
 * Callback listener to determine if object being persisted can have optimized jpa settings.
 */

public class PersistenceChangesTransactionCallbackListener extends AbstractJobTransactionCallbackAdapter {

   private final ThreadLocal<Class< ? >> objectType = new ThreadLocal<Class< ? >>();

   private  Collection<Class< ? >> classesToIgnore;

   private JpaPersistenceEngine persistenceEngine;

    @Override
    public String getCallbackID() {

        return "Persistable Changes Callback";
    }

    @Override
    public void preUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
        jobEntryChanged(targetPersistence);
    }

    private void jobEntryChanged(final Persistable targetPersistence) {
        if (targetPersistence == null) {
            setIgnoreChanges(false);
            objectType.set(null);
            return;
        }
        final Class<? > classOfTarget = targetPersistence.getClass();

        if (classOfTarget == null) {
            setIgnoreChanges(false);
            return;
        }
        if (classOfTarget.equals(objectType.get())) {
            return; //already set, don't need to do anything.
        }
        setIgnoreChanges(getIgnorability(classOfTarget));
        objectType.set(classOfTarget);
    }



    private boolean getIgnorability(final Class<?> classOfTarget) {
        for (final Class<?> clazz : classesToIgnore) {
            if (clazz.isAssignableFrom(classOfTarget)) {
                return true;
            }
        }
        return false;
    }

    private void setIgnoreChanges(final boolean ignoreChanges) {
        getOpenJpaEntityManager().setIgnoreChanges(ignoreChanges);
    }

    private OpenJPAEntityManager getOpenJpaEntityManager() {
        return OpenJPAPersistence.cast(persistenceEngine.getEntityManager());
    }

    @Override
    public void preRemoveJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
        jobEntryChanged(targetPersistence);
    }

    /**
     *
     * @return the classesToIgnore
     */
    public Collection<Class< ? >> getClassesToIgnore() {
        return classesToIgnore;
    }

    /**
     *
     * @param classesToIgnore the classesToIgnore to set
     */
    public void setClassesToIgnore(final Collection<Class< ? >> classesToIgnore) {
        this.classesToIgnore = classesToIgnore;
    }

    /**
     *
     * @return the persistenceEngine
     */
    public JpaPersistenceEngine getPersistenceEngine() {
        return persistenceEngine;
    }

    /**
     *
     * @param persistenceEngine the persistenceEngine to set
     */
    public void setPersistenceEngine(final JpaPersistenceEngine persistenceEngine) {
        this.persistenceEngine = persistenceEngine;
    }
}
