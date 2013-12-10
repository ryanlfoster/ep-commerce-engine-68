package com.elasticpath.tools.sync.job;

import com.elasticpath.persistence.api.Persistable;

/**
 * Interface for the cache that loads the source objects.
 *
 */
public interface SourceObjectCache {

    /**
     * Loads the persistent object from the source into the cache given its guid and class.
     *
     * @param guid the guid of the persistent object in the source.
     * @param clazz the class of the object.
     */
    void load(final String guid, final Class<?> clazz);

    /**
     * Retrieve the persistent object from the cache.
     *
     * @param guid the guid to retrieve.
     * @param clazz the class of the persistent object.
     * @return a persistent object which has the given guid and class type.
     */
    Persistable retrieve(final String guid, final Class<?> clazz);

    /**
     * this indicates if the source object cache allows preloading of the persistent objects( possibly asynchronous).
     *
     * @return true if the object cache supports pre-loading.
     */
    boolean supportsPreloading();

    /**
     * Removes an obect from the cache.
     *
     * @param guid the guid to remove.
     * @param clazz the class of the object to remove.
     */
    void remove(String guid, Class<?> clazz);

}
