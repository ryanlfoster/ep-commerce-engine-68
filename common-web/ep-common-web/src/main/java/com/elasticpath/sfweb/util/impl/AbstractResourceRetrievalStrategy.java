/**
 * 
 */
package com.elasticpath.sfweb.util.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.sfweb.util.AssetResourceRetrievalStrategy;

/**
 * Abstract strategy for retrieving resources from an asset repository.
 */
public abstract class AbstractResourceRetrievalStrategy implements AssetResourceRetrievalStrategy {
	private static final Logger LOG = Logger.getLogger(AbstractResourceRetrievalStrategy.class);
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
	 * Return a URL to the requested resource.
	 * 
	 * @param resourcePath the path to the resource to get.
	 * @return a URL to the resource, or null if the resource is not found.
	 * @throws MalformedURLException if error
	 */
	public URL resolveResource(final String resourcePath) throws MalformedURLException {
		File resourceFile = new File(getFullPath(resourcePath));
		if (resourceFile.exists()) {
			return resourceFile.toURL();
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Resource at \n\t" + resourceFile.getPath() + " not found.");
		}
		return null;
	}
	
	/**
	 * Gets the full file system path to a resource given the partial path.
	 * @param resourcePath the partial path to a resource
	 * @return the full path to a resource
	 */
	public abstract String getFullPath(final String resourcePath);
}
