package com.elasticpath.tools.sync.merge.configuration.impl;

import org.apache.log4j.Logger;
import org.apache.lucene.util.cache.SimpleLRUCache;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;

/**
 *
 * Abstract Entity Locator class.
 *
 */
public abstract class AbstractEntityLocator implements EntityLocator {

	/** Separates category and catalog codes. */
	static final String GUID_SEPARATOR = "|";

	private GuidLocator guidLocator;

	private FetchGroupLoadTuner emptyFetchGroupLoadTuner;

	private SimpleLRUCache refCache;

	private static final Logger LOG = Logger.getLogger(AbstractEntityLocator.class);


	/**
	 * Sets locator retrieving GUIDs of loaded objects.
	 *
	 * @param guidLocator guid retriever instance
	 */
	public void setGuidLocator(final GuidLocator guidLocator) {
		this.guidLocator = guidLocator;
	}

	@Override
	public Persistable locatePersistence(final Persistable sourceValue)
			throws SyncToolConfigurationException {	// NOPMD
				String guid = guidLocator.locateGuid(sourceValue);
				final Class< ? > clazz = sourceValue.getClass();

				if (sourceValue instanceof Category) {
					final String code = ((Category) sourceValue).getCatalog().getCode();
					guid = new StringBuffer(guid).append(GUID_SEPARATOR).append(code).toString();
				}

				return locatePersistence(guid, clazz);
	}


    @Override
   public Persistable locatePersistentReference(final Persistable sourceValue) throws SyncToolConfigurationException {
           String guid = guidLocator.locateGuid(sourceValue);
           final Class< ? > clazz = sourceValue.getClass();

           if (sourceValue instanceof Category) {
               final String code = ((Category) sourceValue).getCatalog().getCode();
               guid = new StringBuffer(guid).append(GUID_SEPARATOR).append(code).toString();
           }


           final Pair<String, Class<?>> key = new Pair<String, Class<?>>(guid, sourceValue.getClass());
          final Persistable cachedRef = (Persistable) getRefCache().get(key);
          if (cachedRef != null) {
              LOG.debug("Persistable reference  cache hit.");
              return cachedRef;
          }

         final Persistable persistentReference = locatePersistentReference(guid, clazz);
         getRefCache().put(key, persistentReference);
         LOG.debug("Persistable reference cache miss.");
        return persistentReference;
   }




    /**
	 * @param clazz object's type cannot be null
	 */
	protected void sanityCheck(final Class< ? > clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("There is a problem locating the type of business object in the change set. "
					+ "Please make sure the type of object is one that is supported.");
		}
	}

	@Override
	public boolean entityExists(final String guid, final Class< ? > clazz) {
	    final Persistable locatePersistence = locatePersistentReference(guid, clazz);

	    if (locatePersistence == null) {
	        return false;
	    }
	    return true;
	}

	/**
	 * {@inheritDoc} <br/><br/>
	 * By default, delegate to the regular locatePersistence() method.  Override this to tune the locator for sorting.
	 */
	public Persistable locatePersistenceForSorting(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		return locatePersistence(guid, clazz);
	}

	@Override
    public Persistable locatePersistentReference(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
        return locatePersistence(guid, clazz);
    }

	/**
	 * Sets the empty {@link FetchGroupLoadTuner}.
	 *
	 * @param emptyFetchGroupLoadTuner the empty {@link FetchGroupLoadTuner}
	 */
	public void setEmptyFetchGroupLoadTuner(final FetchGroupLoadTuner emptyFetchGroupLoadTuner) {
		this.emptyFetchGroupLoadTuner = emptyFetchGroupLoadTuner;
	}

	/**
	 * Gets the empty {@link FetchGroupLoadTuner}.
	 *
	 * @return the empty {@link FetchGroupLoadTuner}
	 */
	public FetchGroupLoadTuner getEmptyFetchGroupLoadTuner() {
		return emptyFetchGroupLoadTuner;
	}

	/**
    *
    * @return the refCache
    */
   public SimpleLRUCache getRefCache() {
       return refCache;
   }

   /**
    *
    * @param refCache the refCache to set
    */
   public void setRefCache(final SimpleLRUCache refCache) {
       this.refCache = refCache;
   }


}