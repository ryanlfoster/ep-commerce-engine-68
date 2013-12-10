/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import com.elasticpath.commons.util.AssetRepository;

/**
 * Extended {@link FileResourceLoader} that adds the ability to resolve the relative paths to
 * absolute paths. The paths must be relative to the application's root folder.
 */
public class ContextAwareFileResourceLoader extends ResourceLoader {

	private static final String PROPERTY_PATH = "path";
	private final FileResourceLoader delegate = new FileResourceLoader();
	private ExtendedProperties configuration;
	private boolean initialized;
	private AssetRepository assetRepository;
	
	@Override
	public void commonInit(final RuntimeServices runtimeServices, final ExtendedProperties configuration) {
		super.commonInit(runtimeServices, configuration);
		delegate.commonInit(runtimeServices, configuration);
	}

	/**
	 * Initializes the configuration by converting all the paths to
	 * absolute paths.
	 * 
	 * @param configuration the configuration properties
	 */
	@Override
	public void init(final ExtendedProperties configuration) {
		this.configuration = configuration;

		delegate.init(configuration);
	}

	@Override
	public InputStream getResourceStream(final String templateName) throws ResourceNotFoundException {
		initializeAssetsPaths();
		return delegate.getResourceStream(templateName);
	}

	/**
	 * Construct the list of base paths for Velocity to look for templates.
	 * This will need to include the themed assets as well as any global assets.
	 */
	private void initializeAssetsPaths() {
		if (!initialized) {
			String baseAssetsPath = assetRepository.getCatalogAssetPath();
			String themedAssets = assetRepository.getThemeAssetsPath();
			configuration.addProperty(PROPERTY_PATH, themedAssets);
			configuration.addProperty(PROPERTY_PATH, baseAssetsPath);
			this.init(configuration);
			initialized = true;
		}
	}

	@Override
	public long getLastModified(final Resource resource) {
		initializeAssetsPaths();
		return delegate.getLastModified(resource);
	}

	@Override
	public boolean isSourceModified(final Resource resource) {
		initializeAssetsPaths();
		return delegate.isSourceModified(resource);
	}
	
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
}
