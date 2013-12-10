/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.util.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.exception.EpWebException;
import com.elasticpath.sfweb.util.AssetResourceRetrievalStrategy;
import com.elasticpath.sfweb.util.FilenameUtils;

/**
 * Strategy to provide virtualized access to resources - images, css, etc. under the theme subdirectory of
 * the assets directory.
 * The final resource returned will depend on:
 * <ul>
 *   <li>the store being accessed</li>
 *   <li>the display theme currently associated with the store</li>
 *   <li>whether the resource exists for the store</li>
 *   <li>whether a fallback option exists in the default of the theme</li>
 * </ul>
 */
public class StoreThemeAssetResourceRetrievalStrategy implements AssetResourceRetrievalStrategy {

	private static final Logger LOG = Logger.getLogger(StoreThemeAssetResourceRetrievalStrategy.class);
	private StoreConfig storeConfig;
	private AssetRepository assetRepository;

	/**
	 * @return the assetRepository
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
	
	/**
	 * @param storeConfig the store config to set
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	/**
	 * Return the store config.
	 *
	 * @return the store config.
	 */
	public StoreConfig getStoreConfig() {
		return this.storeConfig;
	}
	
	/**
	 * Resolve a resource on a store-specific basis, if it doesn't exist 
	 * resolve the resource in the 'default' location.
	 *
	 * @param resourcePath the path to the resource
	 * @return a URL to the resource, could be null if no store-specific 
	 *         or fallback exists.
	 * @throws MalformedURLException if there is a problem creating the URL.
	 */
	public URL resolveResource(final String resourcePath) throws MalformedURLException {
		String theme = getStoreConfig().getSetting("COMMERCE/STORE/theme").getValue();
		
		URL url = getResource(resolvePath(resourcePath, theme,  getStoreConfig().getStoreCode()));
		if (url == null) {
			url = getResource(resolvePathDefault(resourcePath, theme));
			LOG.trace("Falling back to default resource.");
		}
		return url;
	}
	
	
	/**
	 * Return a URL to the requested resource. The path must be absolute. No files
	 * can be retrieved from within a WAR.
	 * 
	 * @param resourcePath the path to the resource to get.
	 * @return a URL to the resource, or null if the resource is not found.
	 * @throws MalformedURLException if error
	 */
	URL getResource(final String resourcePath) throws MalformedURLException {
		File resourceFile = new File(resourcePath);
		if (!resourceFile.isAbsolute()) {
			throw new EpWebException("Must be an absolute path. No file can be retrieved inside web context.");
		}
		if (resourceFile.isFile()) {
			return resourceFile.toURL();
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Resource at \n\t" + resourceFile.getPath() + " for store code "
					+ getStoreConfig().getStoreCode() + " not found.");
		}
		return null;
	}
	
	/**
	 * Resolve the requested path on a store-specific basis.
	 *
	 * @param resourcePath the path we want.
	 * @param theme the theme name
	 * @param storeCode the store code to resolve the request for.
	 * @return the store-specific path to the requested resource.
	 */
	String resolvePath(final String resourcePath, final String theme, final String storeCode) {
		return FilenameUtils.formPath(getAssetRepository().getThemeAssetsPath(), 
			theme, 
			storeCode, 
			resourcePath);
	}
	
	/**
	 * Resolve the requested path using a default path.
	 *
	 * @param resourcePath the path we want.
	 * @param theme the theme to resolve for
	 * @return the path to the requested resource.
	 */
	String resolvePathDefault(final String resourcePath, final String theme) {
		return resolvePath(resourcePath, theme, "default");
	}


	
}
