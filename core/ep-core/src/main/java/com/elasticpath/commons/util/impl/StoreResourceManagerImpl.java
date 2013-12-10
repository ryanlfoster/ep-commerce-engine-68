/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.commons.util.StoreVelocityConfigHelper;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * Provides a resource manager for velocity that can serve store-specific resources with a fallback strategy.
 */
public class StoreResourceManagerImpl extends ResourceManagerImpl implements InvalidatableCache {

	private static final Logger LOG = Logger.getLogger(StoreResourceManagerImpl.class);

	private static StoreConfig storeConfig;

	private static ConcurrentMap<String, StoreResourceManagerImpl> instanceMap = new ConcurrentHashMap<String, StoreResourceManagerImpl>();

	private final ResourceLocationCache resourceLookupCache = new ConcurrentResourceLocationCache();

	private final ResourceLoaderNameCache resourceLoaderNameCache = new ConcurrentResourceLoaderNameCache();

	private AssetRepository assetRepository;

	/**
	 * Default Constructor.
	 */
	protected StoreResourceManagerImpl() {
		super();
	}

	/**
	 * Return the singleton <code>StoreResourceManagerImpl</code>.
	 *
	 * @return the singleton <code>StoreResourceManagerImpl</code>
	 */
	public static ResourceManager getInstance() {
		String instanceKey = "";
		if (storeConfig != null && storeConfig.getStoreCode() != null) {
			instanceKey = storeConfig.getStoreCode();
		}

		if (!instanceMap.containsKey(instanceKey)) {
			instanceMap.putIfAbsent(instanceKey, new StoreResourceManagerImpl());
		}
		
		return instanceMap.get(instanceKey); 
	}

	/**
	 * Gets the named resource. Returned class type corresponds to specified type (i.e. <code>Template</code> to <code>
	 * RESOURCE_TEMPLATE</code>). Will
	 * attempt to find a store-specific version of the resource first and then fall back to the default.
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @return Resource with the template parsed and ready.
	 * @throws Exception if a problem in parse
	 */
	@Override
	public Resource getResource(final String resourceName, final int resourceType, final String encoding) throws Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Resource being requested from manager: " + resourceName);
		}

		Resource resource = getResourceFromLastResolvedLocation(resourceName, resourceType, encoding);

		if (resource != null) {
			return resource;
		}

		resource = getResourceUsingFallback(resourceName, resourceType, encoding);
		return getResourceLookupCache().put(resourceName, resource);
	}

	/**
	 * Retrieves the specified resource using the following fallback algorithm:
	 * <ol>
	 * <li>Is storeConfig null?
	 * <ul>
	 * <li>return resource found through standard velocity resource location algorithm</li>
	 * </ul>
	 * </li>
	 * <li>Is storeConfig's store code null?
	 * <ul>
	 * <li>attempt to find resource in cm assets, else return resource found through standard velocity resource location algorithm</li>
	 * </ul>
	 * </li>
	 * <li>Attempt to find resource in store-specific theme assets.</li>
	 * <li>Attempt to find resource in default theme assets.</li>
	 * <li>Return resource found through standard velocity resource location algorithm.</li>
	 * </ol>
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @return the resolved resource
	 * @throws ResourceNotFoundException if template not found from any available source.
	 * @throws Exception if a problem in parse
	 */
	protected Resource getResourceUsingFallback(final String resourceName, final int resourceType, final String encoding)
			throws Exception {
		final StoreConfig storeConfig = getStoreConfig();

		if (storeConfig == null) {
			// If we don't have a store config we should use the standard velocity
			// resource location algorithm.
			return getResourceFromParent(resourceName, resourceType, encoding);
		} else if (storeConfig.getStoreCode() == null) {
			// To access global CM resources - a storeConfig must be provided
			// but store code must be null.
			Resource cmResource = getResourceCMGlobal(resourceName, resourceType, encoding);
			if (cmResource != null) {
				return cmResource;
			}

			// Fall back onto parent loader
			return getResourceFromParent(resourceName, resourceType, encoding);
		}

		final String theme = getStoreTheme();
		Resource resource = getResourceStoreSpecific(resourceName, resourceType, encoding, theme);
		if (resource == null) {
			resource = getResourceFromThemeDefault(resourceName, resourceType, encoding, theme);
			if (resource == null) {
				resource = getResourceFromParent(resourceName, resourceType, encoding);
			}
		}
		return resource;
	}

	/**
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @return the resolved resource
	 * @throws Exception if there is an error locating the resource.
	 */
	protected Resource getResourceFromLastResolvedLocation(final String resourceName, final int resourceType, final String encoding)
			throws Exception {
		return getResourceLookupCache().get(resourceName, resourceType, encoding);
	}

	/**
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @param theme the theme to search for the resource in
	 * @return the resolved resource
	 * @throws Exception if there was a error getting the resource
	 */
	protected Resource getResourceStoreSpecific(final String resourceName, final int resourceType, final String encoding, final String theme)
			throws Exception {
		final String storeSpecificResourcePath = StoreVelocityConfigHelper.getStoreSpecificResourcePath(resourceName, theme, storeConfig
				.getStoreCode());

		Resource resource;
		if (LOG.isTraceEnabled()) {
			LOG.trace("Attempting to retrieve store-specific resource: " + storeSpecificResourcePath);
		}
		resource = getResourceInternal(storeSpecificResourcePath, resourceType, encoding);
		return resource;
	}

	/**
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @param theme the theme to search for the resource in
	 * @return the resolved resource
	 * @throws Exception if there was a error getting the resource
	 */
	protected Resource getResourceFromThemeDefault(final String resourceName, final int resourceType, final String encoding, final String theme)
			throws Exception {
		Resource resource;
		final String defaultResourcePath = StoreVelocityConfigHelper.getDefaultResourcePath(resourceName, theme);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Store-specific resource not found, attempting to use fallback: " + defaultResourcePath);
		}
		resource = getResourceInternal(defaultResourcePath, resourceType, encoding);
		return resource;
	}

	/**
	 * Resolves resource using default velocity location algorithm.
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @return the resolved resource
	 * @throws ResourceNotFoundException if template not found from any available source.
	 * @throws Exception if a problem in parse
	 */
	protected Resource getResourceFromParent(final String resourceName, final int resourceType, final String encoding) throws Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Attempting to use resource manager default resource loader");
		}
		return super.getResource(resourceName, resourceType, encoding);
	}

	/**
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @return the resolved resource
	 * @throws Exception if there was a error getting the resource
	 */
	protected Resource getResourceCMGlobal(final String resourceName, final int resourceType, final String encoding) throws Exception {
		final String cmResourcePath = StoreVelocityConfigHelper.getCMResourcePath(resourceName, getCMAssetsDir());
		if (LOG.isTraceEnabled()) {
			LOG.trace("Attempting to retrieve global resource: " + cmResourcePath);
		}
		return getResourceInternal(cmResourcePath, resourceType, encoding);
	}

	/**
	 * Gets the named resource. Returned class type corresponds to specified type (i.e. <code>Template</code> to <code>RESOURCE_TEMPLATE</code>).
	 * Calls the <code>ResourceManagerImpl</code> "getResource" method but handle <code>REsourceNotFoundException</code> exceptions and return null
	 * instead if the resource cannot be found, to allow easy fallback.
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>, <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding The character encoding to use.
	 * @return Resource with the template parsed and ready, or null if it could not be found.
	 * @throws Exception if a problem in parse
	 */
	protected Resource getResourceInternal(final String resourceName, final int resourceType, final String encoding) throws Exception {

		Resource resource = null;
		try {
			// Check that a loader can get the resource before getting it. Avoids an un helpful error in log.
			if (getLoaderNameForResource(resourceName) == null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Resource not found: " + resourceName);
				}
			} else {
				resource = super.getResource(resourceName, resourceType, encoding);
			}
		} catch (ResourceNotFoundException rnfe) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Resource not found: " + resourceName);
			}
		}
		return resource;
	}

	/**
	 * <p>
	 * Determines if a template exists, and returns the name of the loader that provides it.
	 * </p>
	 * <p>
	 * This method will first check the global cache for the template and get the loader from there. If the resource can not be found in the cache it
	 * falls back to the default behavior of searching all resource loaders.
	 * </p>
	 *
	 * @param resourceName Name of template or content resource
	 * @return class name of loader than can provide it
	 */
	@Override
	public String getLoaderNameForResource(final String resourceName) {
		String loaderName = getResourceLoaderNameCache().get(resourceName);
		if (loaderName != null) {
			if (ResourceLoaderNameCache.NO_LOADER.equals(loaderName)) {
				return null;
			}
			return loaderName;
		}
		return getResourceLoaderNameCache().put(resourceName, super.getLoaderNameForResource(resourceName));
	}

	/**
	 * Get the current store's theme.
	 *
	 * @return the store's theme
	 */
	String getStoreTheme() {
		return getStoreConfig().getSetting("COMMERCE/STORE/theme").getValue();
	}

	/**
	 * Get the CM specific assets directory for global resources.
	 *
	 * @return the globals directory
	 */
	String getCMAssetsDir() {
		return getAssetRepository().getCmAssetsSubfolder();
	}

	/**
	 * Get a reference to the store configuration.
	 *
	 * @return the StoreConfig object
	 */
	protected StoreConfig getStoreConfig() {
		return storeConfig;
	}

	/**
	 * @param storeConfigParam the storeConfig to set
	 */
	public void setStoreConfig(final StoreConfig storeConfigParam) {
		storeConfig = storeConfigParam;
	}

	/**
	 * @return the assetRepository
	 */
	protected AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	/**
	 * Cache for where requested resources where actually resolved from.
	 */
	protected interface ResourceLocationCache {
		/**
		 * Puts a resource into the resource location cache.
		 *
		 * @param resourceName the resource that was requested
		 * @param resource the resource that was resolved
		 * @return the same resource that was passed in
		 */
		Resource put(String resourceName, Resource resource);

		/**
		 * Gets a resource using the resource location cache.
		 *
		 * @param resourceName the resource being requested
		 * @param resourceType the type of resource
		 * @param encoding the resource encoding
		 * @return the resolved resource or <code>null</code> if not found
		 * @throws Exception if an error occurred during resource retrieval
		 */
		Resource get(String resourceName, final int resourceType, final String encoding) throws Exception;

		/**
		 * Clears all entries from the resource lookup cache.
		 */
		void clear();
	}

	/**
	 * Thread-safe implementation of {@link ResourceLocationCache}, backed by a {@link ConcurrentHashMap}.
	 */
	protected class ConcurrentResourceLocationCache implements ResourceLocationCache {
		private final Map<String, String> resourceCache = new ConcurrentHashMap<String, String>();

		/**
		 * Gets the requested resource from the location stored in the cache. If the resource was in the cache, but it can no longer be found at that
		 * location, then the cache entry is removed.
		 *
		 * @param resourceName the resource being requested
		 * @param resourceType the type of resource
		 * @param encoding the resource encoding
		 * @return the resolved resource or <code>null</code> if not found
		 * @throws Exception if an error occurred during resource retrieval
		 */
		public Resource get(final String resourceName, final int resourceType, final String encoding) throws Exception {
			String resourcePath = resourceCache.get(resourceName);

			if (resourcePath == null) {
				return null;
			}

			Resource resource = getResourceInternal(resourceCache.get(resourceName), resourceType, encoding);
			if (resource != null) {
				return resource;
			}
			// Couldn't actually locate the resource (despite having been previously found at the cached location), use the
			// standard fallback algorithm to do best effort in finding this resource.
			resourceCache.remove(resourceName);
			return null;
		}

		/**
		 * Puts a resource's resolved location into the resource location cache. If the resource is <code>null</code> then no entry is added to the
		 * cache.
		 *
		 * @param resourceName the resource that was requested
		 * @param resource the resource that was resolved
		 * @return the same resource that was passed in
		 * @throws NullPointerException if the resourceName is <code>null</code>
		 */
		public Resource put(final String resourceName, final Resource resource) {
			if (resource == null) {
				// Resource could not be found - won't cache that fact.
				return null;
			}
			resourceCache.put(resourceName, resource.getName());
			return resource;
		}

		@Override
		public void clear() {
			resourceCache.clear();
		}
	}

	/**
	 * Cache for keeping track of which resource loader was able to load which resources and which resources were not able to be loaded from any
	 * resource loader.
	 */
	protected interface ResourceLoaderNameCache {
		/**
		 * String that represents the fact that no loader could be found for the specified resource.
		 */
		String NO_LOADER = "NO_LOADER";

		/**
		 * Puts an entry into the resource loader name cache.
		 *
		 * @param resourceName the resource being requested
		 * @param resourceLoaderName the name of the loader that it was found in
		 * @return the resourceLoaderName that was passed in
		 */
		String put(final String resourceName, final String resourceLoaderName);

		/**
		 * Gets the name of the resource loader that was previously able to resolve this resource.
		 *
		 * @param resourceName the resource being requested
		 * @return the name of the resource loader that was previously able to load the specified resource, <code>NO_LOADER</code> if the resource
		 *         was not able to be resolved previously, or <code>null</code> if no entry is present in the cache.
		 */
		String get(final String resourceName);

		/**
		 * Clears all entries from the resource loader name cache.
		 */
		void clear();
	}

	/**
	 * Thread-safe implementation of {@link ResourceLoaderNameCache}, backed by a {@link ConcurrentHashMap}.
	 */
	protected class ConcurrentResourceLoaderNameCache implements ResourceLoaderNameCache {
		private final Map<String, String> loaderNameCache = new ConcurrentHashMap<String, String>();

		/**
		 * Puts an entry into the resource loader name cache. Keeps track of loaders that are null.
		 *
		 * @param resourceName the resource being requested
		 * @param resourceLoaderName the name of the loader that it was found in
		 * @return the resourceLoaderName that was passed in
		 */
		public String put(final String resourceName, final String resourceLoaderName) {
			loaderNameCache.put(resourceName, StringUtils.defaultString(resourceLoaderName, NO_LOADER));
			return resourceLoaderName;
		}

		@Override
		public String get(final String resourceName) {
			return loaderNameCache.get(resourceName);
		}

		@Override
		public void clear() {
			loaderNameCache.clear();
		}
	}

	/**
	 * @return the resourceLookupCache
	 */
	protected ResourceLocationCache getResourceLookupCache() {
		return resourceLookupCache;
	}

	/**
	 * @return the resourceLoaderNameCache
	 */
	protected ResourceLoaderNameCache getResourceLoaderNameCache() {
		return resourceLoaderNameCache;
	}

	/**
	 * Invalidates all cached resources for all stores.
	 * This method should be consistent with the one in the {@link StoreVelocityConfigurer}
	 */
	public void invalidate() {
		instanceMap.clear();
	}

	/** 
	 * Invalidates all cached resources for a store.
	 *
	 * @param objectUid - the storeCode that will be cleared from the cache.
	 */
 	public void invalidate(final Object objectUid) {
		final String instanceKey = (String) objectUid;
	
		// there will be in the map an object with instanceKey="" that was created by spring,
		// when the bean "storeResourceManager" get initialized
		// this should not happen
		if (StringUtils.isEmpty(instanceKey)) {
			LOG.debug("Invalidate got called for store with key: " + instanceKey);
			return;	
		}

		
		instanceMap.remove(instanceKey);
		

 	}
}
