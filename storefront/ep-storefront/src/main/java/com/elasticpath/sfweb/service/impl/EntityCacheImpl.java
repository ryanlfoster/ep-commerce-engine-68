package com.elasticpath.sfweb.service.impl;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.beanutils.PropertyUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.sfweb.service.EntityCache;

/**
 * Generic cache that caches entities or other objects by multiple keys.
 *
 * @param <P> The persistable object type being cached
 */
public class EntityCacheImpl<P extends Persistable> implements EntityCache<P> {

	private String entityName;
	private List<String> keyProperties;
	private Ehcache cache;
	private PersistenceEngine persistenceEngine;

	@Override
	public P put(final P obj) throws EpServiceException {
		if (obj == null) {
			throw new IllegalArgumentException("Cannot cache null objects in cache: " + getEntityName());
		}

		P detached = getPersistenceEngine().detach(obj);
		for (String keyName : getKeyProperties()) {
			try {
				final Object key = PropertyUtils.getProperty(detached, keyName);
				if (key != null) {
					final String cacheKey = createCacheKey(keyName, key);

					getCache().put(new Element(cacheKey, detached));
				}
			} catch (Exception ex) {
				throw new EpServiceException("Could not cache object " + detached, ex);
			}
		}

		return detached;
	}

	@Override
	@SuppressWarnings("unchecked")
	public P get(final String keyName, final Object keyValue) {
		String cacheKey = createCacheKey(keyName, keyValue);

		final Element cacheElement = getCache().get(cacheKey);
		if (cacheElement == null) {
			return null;
		}
		return (P) cacheElement.getObjectValue();
	}

	/**
	 * Creates a key used to get and put objects in the underlying cache.
	 *
	 * @param keyName the name of the key property
	 * @param key the value of the key
	 *
	 * @return a unique key used to stash the objects in the underlying cache
	 */
	protected String createCacheKey(final String keyName, final Object key) {
		StringBuilder sb = new StringBuilder(); // NOPMD
		if (getEntityName() != null) {
			sb.append(getEntityName()).append('-');
		}

		sb.append(keyName).append('-');
		sb.append(key.toString());

		return sb.toString();
	}

	@Override
	public void invalidate() {
		getCache().removeAll();
	}

	@Override
	public void invalidate(final Object keyValue) {
		invalidate();
	}

	protected String getEntityName() {
		return entityName;
	}

	public void setEntityName(final String entityName) {
		this.entityName = entityName;
	}

	protected List<String> getKeyProperties() {
		return keyProperties;
	}

	public void setKeyProperties(final List<String> keyProperties) {
		this.keyProperties = keyProperties;
	}

	protected Ehcache getCache() {
		return cache;
	}

	public void setCache(final Ehcache cache) {
		this.cache = cache;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
