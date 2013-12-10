package com.elasticpath.commons.util.impl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.commons.util.MessageSourceCache;
import com.elasticpath.commons.util.StoreMessageSource;
import com.elasticpath.settings.SettingsReader;

/**
 * Loads messages specific to stores, defined as part of the store's theme.
 */
public class StoreMessageSourceImpl implements StoreMessageSource {

	private static final Logger LOG = Logger.getLogger(StoreMessageSourceImpl.class);

	private static final String NOT_FOUND = null;

	private MessageSourceCache messageSourceCache;

	private MessageSource globalMessageSource;

	private AssetRepository assetRepository;
	private SettingsReader settingsReader;


	@Override
	public String getMessage(final String storeCode, final String messageCode, final Locale locale) {
		return getMessage(storeCode, getStoreThemeCode(storeCode), messageCode, locale);
	}

	private String getStoreThemeCode(final String storeCode) {
		return settingsReader.getSettingValue("COMMERCE/STORE/theme", storeCode).getValue();
	}

	/**
	 * {@inheritDoc}
	 * If the store-theme-specific message does not exist, then the general one matching the given code will be returned.
	 *
	 */
	public String getMessage(final String storeCode, final String themeCode, final String messageCode, final Locale locale) {
        try {
			if (storeCode == null) {
	        	return getCMMessage(messageCode, locale);
	        }

			String value = getCachedMessage(storeCode, themeCode, messageCode, locale);
	        if (value != null) {
	        	return value;
	        }

	        // fall back to global message source for messages that are not store specific
	        return getGlobalMessage(messageCode, locale);

	    } catch (Exception e) {
	    	LOG.error(e);

	    	return NOT_FOUND;
	    }
	}

	/**
	 * Retrieve a cached message value.
	 * @param storeCode the store code
	 * @param themeCode the theme code
	 * @param messageCode the message code
	 * @param locale the locale
	 * @return the cached message, or null if one cannot be found that matches the inputs.
	 */
	String getCachedMessage(final String storeCode, final String themeCode, final String messageCode, final Locale locale) {
		return messageSourceCache.getProperty(themeCode, storeCode, messageCode, locale);
	}

	/**
	 * Set the global message source.
	 *
	 * @param globalMessageSource The message source to use as the global.
	 */
	public void setGlobalMessageSource(final MessageSource globalMessageSource) {
		this.globalMessageSource = globalMessageSource;
	}

	/**
	 * Retrieve a global message value.
	 * @param messageCode the message code
	 * @param locale the locale
	 * @return the message
	 */
	String getGlobalMessage(final String messageCode, final Locale locale) {
		try {
			return globalMessageSource.getMessage(messageCode, new Object[0], locale);

		} catch (NoSuchMessageException e) {
			return NOT_FOUND;
		}
	}

	/**
	 * Gets the message as a standard CM message.
	 * @param code the message code
	 * @param locale the locale
	 * @return the message
	 */
	String getCMMessage(final String code, final Locale locale) {
		try {
			//Attempt to retrieve as cm message
			//cmAsset properties will be stored with the subfolder name setting as key
			String cmAssets = getAssetRepository().getCmAssetsSubfolder();
			return messageSourceCache.getProperty(cmAssets, "", code, locale);
		} catch (NoSuchMessageException e) {
			LOG.error(e);
			return NOT_FOUND;
		}
	}

	/**
	 * Set the message source cache.
	 *
	 * @param messageSourceCache The message source cache.
	 */
	public void setMessageSourceCache(final MessageSourceCache messageSourceCache) {
	    this.messageSourceCache = messageSourceCache;
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
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
}
