package com.elasticpath.tools.sync.job.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

import org.springframework.core.task.TaskExecutor;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.tools.sync.job.SourceObjectCache;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 * An asynchronous version of the source object cache.
 */
public class SourceObjectCacheImpl implements SourceObjectCache {

    private EntityLocator entityLocator;

    private TaskExecutor sourceObjectLoadingTaskExecutor;


    private final ConcurrentHashMap<Pair<String, Class<?>>, Future<Persistable>> cache = new ConcurrentHashMap<Pair<String, Class<?>>,
            Future<Persistable>>();

    private Semaphore availablePermits;

    /**
     * the Callable (Asynchronous) class which loads the persistable into a {@code Future } object.
     */
    class EntityLoadingCallable implements Callable<Persistable> {

        private final String guid;

        private final Class<?> type;

        private final EntityLocator entityLocator;

        private Persistable persistable;

        /**
         * Constructor for the callable.
         *
         * @param entityLocator The locator to use, or the delegating locator.
         * @param guid the guid to locate.
         * @param type the class type to locate.
         */
        public EntityLoadingCallable(final EntityLocator entityLocator, final String guid, final Class<?> type) {
            this.entityLocator = entityLocator;
            this.guid = guid;
            this.type = type;
        }

        /**
         * This method is called by the executor asynchronously.
         *
         * @return the Persistent object from the entity locator.
         * @throws Exception possibly throws an exception.
         */
        @Override
        public Persistable call() throws Exception {
            // here's where we use that semaphore (acq)
            availablePermits.acquire();
            persistable = entityLocator.locatePersistence(guid, type);
            return persistable;
        }
    }

    /**
     * Asynchronously loads the {@code Future<Persistent>} object into the cache.
     *
     * @param guid the guid to load.
     * @param clazz the class of the guid.
     */
    public void load(final String guid, final Class<?> clazz) {
        final EntityLoadingCallable loader = new EntityLoadingCallable(entityLocator, guid, clazz);
        final FutureTask<Persistable> future = new FutureTask<Persistable>(loader);
        cache.put(new Pair<String, Class<?>>(guid, clazz), future);
        sourceObjectLoadingTaskExecutor.execute(future);

    }

    /**
     * Retrieves the Persistent object based on the guid/class. Will be idempotent.
     *
     * @param guid the guid.
     * @param clazz the class of the guid.
     * @return the persistent object.
     */
    public Persistable retrieve(final String guid, final Class<?> clazz) {
        try {
            final Pair<String, Class<?>> pair = new Pair<String, Class<?>>(guid, clazz);
            final Future<Persistable> future = cache.get(pair);
            return future.get();
        } catch (final Exception e) {
            throw new EpServiceException("Cache miss--unable to fetch from the future!  Guid: " + guid + ", Type: " + clazz, e);
        }
    }

    /**
     * Removes the guid/class-persistent from the cache.
     *
     * @param guid the guid of the object.
     * @param clazz the class.
     */
    public void remove(final String guid, final Class<?> clazz) {
        final Pair<String, Class<?>> pair = new Pair<String, Class<?>>(guid, clazz);
        cache.remove(pair);
        availablePermits.release(); //release semaphore
    }
    /**
     * Indicates whether this implementation supports preloading. ( it dues)
     *
     * @return true since we support preloading (asynchronously) from the db.
     */
    public boolean supportsPreloading() {
        return true;
    }

    /**
     * @param entityLocator the entityLocator to set
     */
    public void setEntityLocator(final EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }

    /**
     * @param sourceObjectLoadingTaskExecutor the sourceObjectLoadingTaskExecutor to set
     */
    public void setSourceObjectLoadingTaskExecutor(final TaskExecutor sourceObjectLoadingTaskExecutor) {
        this.sourceObjectLoadingTaskExecutor = sourceObjectLoadingTaskExecutor;
    }

    /**
     *
     * @return the availablePermits
     */
    public Semaphore getAvailablePermits() {
        return availablePermits;
    }

    /**
     *
     * @param availablePermits the availablePermits to set
     */
    public void setAvailablePermits(final Semaphore availablePermits) {
        this.availablePermits = availablePermits;
    }





}
