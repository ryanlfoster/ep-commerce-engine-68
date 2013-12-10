/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.controller.impl;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.AssetRepository;

/**
 * Resizes images that may live anywhere under the assets folder.
 */
public class AssetImageControllerImpl extends ImageControllerImpl {

	private AssetRepository assetRepository;
	
	/**
	 * Gets the path prefix given the sub folder. 
	 * If a non-blank sub folder is specified, the path will be absolutePathPrefix/subFolder.
	 * For a blank sub folder, the path will be absolutePathPrefix.
	 * 
	 * @param subFolder the sub folder
	 * @return the path prefix for sub folder
	 */
	protected String getPathPrefixForSubFolder(final String subFolder) {
		if (StringUtils.isBlank(subFolder)) {
			return getImageService().getImagePath();
		}
		return getAssetRepository().getCatalogAssetPath() + File.separator + subFolder;
	}

	/**
	 * Sets the Assets Repository.
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	protected AssetRepository getAssetRepository() {
		return assetRepository;
	}
}
