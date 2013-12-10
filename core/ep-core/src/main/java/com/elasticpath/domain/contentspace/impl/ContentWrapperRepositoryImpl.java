/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.contentspace.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperLoader;
import com.elasticpath.domain.contentspace.ContentWrapperRepository;
import com.elasticpath.settings.SettingsReader;

/**
 * The implementation of the content wrapper repository.
 */
public class ContentWrapperRepositoryImpl implements ContentWrapperRepository, InvalidatableCache {

	private static final Logger LOG = Logger.getLogger(ContentWrapperRepositoryImpl.class);

	private Map<String, ContentWrapper> contentWrappers;

	private Map<String, Long> missingContentWrappers = new ConcurrentHashMap<String, Long>();

	private ContentWrapperLoader contentWrapperLoader;

	private SettingsReader settingsReader;

	private static final long MILLI = 1000L;

	/**
	 * The last time (in milliseconds) the content wrappers were loaded.
	 */
	private long lastLoadTime = 0;

	/**
	 * Loads content wrappers if cache is stale.
	 */
	protected void init() {
		if (hasLoadingIntervalElapsed(getLastLoadTime())) {
			reload();
		}
	}

	/**
	 * Method that loads content wrappers using the content wrapper loader,
	 * sets the last load time to the current time.
	 */
	protected void reload() {
		//Set the last load time to reduce contention in the synchronized method
		//by having the hasLoadingIntervalElapsed method return false while the
		//loadContentWrappers method is in progress
		synchronized (this) {
			contentWrappers = contentWrapperLoader.loadContentWrappers();
			lastLoadTime = System.currentTimeMillis();
		}
	}

	/**
	 * Sets the cache of missed content wrappers.
	 *
	 * @param missingContentWrappers map of wrapper Id's with the time that load was attempted.
	 */
	void setMissingContentWrappers(final Map<String, Long> missingContentWrappers) {
		this.missingContentWrappers = missingContentWrappers;
	}

	/**
	 * Returns whether the content wrappers need to be loaded or reloaded i.e.
	 * the cache is stale.
	 *
	 * @param lastLoadTime the time at which the lastLoad was attempted
	 * @return true if content wrappers have not been loaded previously, or interval has been exceeded and false otherwise
	 */
	protected boolean hasLoadingIntervalElapsed(final long lastLoadTime) {
        if (lastLoadTime == 0) {
			// no previous load of content wrappers occurred, therefore loading is required
			return true;
		}

    	// determine the time at which a reload is allowed
    	long reloadTime = lastLoadTime + (getLoadInterval() * MILLI);

    	// the current time must be passed the time a reload is allowed
    	long now = System.currentTimeMillis();
    	return now > reloadTime;
	}

	/**
	 * Returns the length of time that must elapse before a reload of content wrappers is allowed.
	 * @return interval that must elapse before a reload in seconds
	 */
	protected int getLoadInterval() {
		return Integer.valueOf(settingsReader.getSettingValue("COMMERCE/SYSTEM/CONTENTWRAPPERS/reloadInterval").getValue());
	}

	/**
	 * Sets the last load time.
	 *
	 * @param lastLoadTime the last load time in milliseconds
	 */
	protected void setLastLoadTime(final long lastLoadTime) {
		this.lastLoadTime = lastLoadTime;
	}

	/**
	 * Returns the last load time.
	 * @return last load time in milliseconds
	 */
	protected long getLastLoadTime() {
		return lastLoadTime;
	}

	/**
	 * Find a content wrapper by Id, if the content wrapper can not be found the
	 * method will return null.
	 *
	 * @param contentWrapperId the content wrapper Id to be found
	 * @return a content wrapper with specified Id, otherwise null if not found.
	 */
	public ContentWrapper findContentWrapperById(final String contentWrapperId) {
		init();
		ContentWrapper wrapper = contentWrappers.get(contentWrapperId);

		//If the content wrapper has not been found and/or loaded
		if (wrapper == null) {

			//Check if missed cache entry exists and is still young
			if (missingContentWrappers.containsKey(contentWrapperId)
					&& !(hasLoadingIntervalElapsed(missingContentWrappers.get(contentWrapperId)))) {
				LOG.debug("Content wrapper miss contains key and is not expired.");
				return null;
			}

			//Check the file system for the content wrapper if missed cache entry doesn't exist or is old
			reload();
			wrapper = contentWrappers.get(contentWrapperId);
			if (wrapper == null) {
				LOG.debug("Content wrapper miss cache inserted with content wrapper: " + contentWrapperId);
				missingContentWrappers.put(contentWrapperId, System.currentTimeMillis());
				return wrapper;
			}
		}

		//Remove from the missing content wrappers cache if it exists, because it now
		//exists in the loaded wrappers cache
		missingContentWrappers.remove(contentWrapperId);
		return wrapper;
	}

	/**
	 * Returns a map of content wrappers that are currently loaded.
	 *
	 * @return a map of wrapper Ids linked to wrappers objects currently loaded, null if empty
	 */
	public Map<String, ContentWrapper> getContentWrappers() {
		return getContentWrappers(false);
	}

	/**
	 * Returns all content wrappers within the repository.
	 * @param forceReload refresh content wrapper repository
	 * before return content wrappers if true.
	 * @return all wrapper Id's mapped to content wrapper objects, null if there are none
	 */
	public Map<String, ContentWrapper> getContentWrappers(final boolean forceReload) {
		if (forceReload) {
			reload();
		}
		return contentWrappers;
	}


	/**
	 * Sets the content wrappers for the repository.
	 *
	 * @param contentWrappers the content wrappers to be used for the repository
	 */
	public void setContentWrappers(final Map<String, ContentWrapper> contentWrappers) {
		this.contentWrappers = contentWrappers;
	}

	/**
	 * Sets the content wrapper loader, which will be used in loading content wrappers.
	 *
	 * @param contentWrapperLoader the content wrapper loaded used to load wrappers.
	 */
	public void setContentWrapperLoader(final ContentWrapperLoader contentWrapperLoader) {
		this.contentWrapperLoader = contentWrapperLoader;
	}


	/**
	 * The setter for the settings reader.
	 * @param settingsReader the settings reader to be set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Invalidate the currently loaded content wrappers by setting the last load time to zero which
	 * will initiate a reload.
	 */
	public void invalidate() {
		LOG.info("Content wrapper repository cache invalidated");
		lastLoadTime = 0;
	}

	@Override
	public void invalidate(final Object objectUid) {
		// TODO Auto-generated method stub

	}


}
