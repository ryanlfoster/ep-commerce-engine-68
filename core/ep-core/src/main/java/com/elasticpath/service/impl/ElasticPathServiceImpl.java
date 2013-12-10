/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.impl;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.ElasticPathService;
import com.elasticpath.web.ajax.dwrconverter.EpCurrencyConverter;

/**
 * Provides the ability to access elastic path context.
 */
public class ElasticPathServiceImpl extends AbstractEpServiceImpl implements ElasticPathService {
	private static final Logger LOG = Logger.getLogger(ElasticPathServiceImpl.class.getName());

	private AssetRepository assetRepository;
	
	private final Object sfUrlLock = new Object();

	private String storeFrontContextUrl;

	/**
	 * Initialize the elastic path context.
	 */
	public void init() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("initializing...");
		}

		// BEGIN_WLS_VALIDATION_CODE
        // END_WLS_VALIDATION_CODE

		//Prevent application server startup without license checking
		EpCurrencyConverter licenseChecker = new EpCurrencyConverter();
		licenseChecker.setAssetRepository(getAssetRepository());
		licenseChecker.checkLicense();
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
	
	/**
	 * Sets the context url for the storefront. Adds a preceeding slash if missing;
	 * @param storeFrontContextUrl The url to set.
	 */
	public void setStorefrontContextUrl(final String storeFrontContextUrl) {
		synchronized (sfUrlLock) {
			String tempSfUrl = storeFrontContextUrl;
			if (tempSfUrl == null) {
				tempSfUrl = "";
			} else if (tempSfUrl.length() > 0 && !tempSfUrl.startsWith("/")) {
				tempSfUrl = "/" + tempSfUrl;
			}
			this.storeFrontContextUrl = tempSfUrl;
		}
	}
	
	/**
	 * Return the store front context URL.
	 * 
	 * @return the store front context URL
	 */
	public String getStorefrontContextUrl() {
		return this.storeFrontContextUrl;
	}
}
