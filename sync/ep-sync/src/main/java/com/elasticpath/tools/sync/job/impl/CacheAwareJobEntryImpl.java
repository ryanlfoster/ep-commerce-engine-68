package com.elasticpath.tools.sync.job.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.SourceObjectCache;

/**
 * This is a {@code JobEntryImpl} however, it will check in the {@code SourceObjectCacheImpl} for the {@code Persistable} by overriding
 * {@code #getSourceObject()} to retrieve that object. It is expected that the {@code Persistable} will be handled by the
 * {@code SourceObjectCacheImpl} likely asynchronously.
 */
public class CacheAwareJobEntryImpl extends JobEntryImpl {

	private static final long serialVersionUID = 550684978206623062L;

	private final SourceObjectCache cache;

    private Persistable cachedCopyOfpersistence = null;

    /**
     * Constructor which takes the source object cache.
     *
     * @param cache the cache.
     */
    public CacheAwareJobEntryImpl(final SourceObjectCache cache) {
        this.cache = cache;
    }

    /**
     * @return the source object.
     */
    @Override
    public Persistable getSourceObject() {
        if (cachedCopyOfpersistence == null) {
            cachedCopyOfpersistence = cache.retrieve(getGuid(), getType());
        }
        return cachedCopyOfpersistence;
    }

    /**
     * Note:this hshuold never be called.
     * @param sourceObject the source object.
     */
    @Override
    public void setSourceObject(final Persistable sourceObject) {
        throw new IllegalArgumentException("CacheAwareJobEntryImpl doesn't support having the SourceObject set on it directly.");
    }

    private void readObject(final ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        cachedCopyOfpersistence = (Persistable) inStream.readObject();
    }

    private void writeObject(final ObjectOutputStream outStream) throws IOException {
        final Persistable sourceObject = getSourceObject();
        outStream.writeObject(sourceObject);


    }

}
