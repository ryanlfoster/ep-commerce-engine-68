/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationLoader;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationParser;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Loads FilteredNavigationConfiguration (FNC) String from the SettingsService,
 * parses it with the injected parser, and populates a new FNC object. Assumes
 * that the SettingsService will return the String encoded as UTF-8.
 */
public class FilteredNavigationConfigurationLoaderImpl implements FilteredNavigationConfigurationLoader {
	private static final Logger LOG = Logger.getLogger(FilteredNavigationConfigurationLoaderImpl.class);
	
	private SettingsReader settingsReader;
	
	private BeanFactory beanFactory;
	
	/** Injected parser implementation that knows how to parse the configuration document. */
	private FilteredNavigationConfigurationParser parser;
	
	/** The setting definition path to load the configuration from. */
	private String settingKeyPath;
	
	private final Map<String, Pair<String, FilteredNavigationConfiguration>> fncCache = 
		new HashMap<String, Pair<String, FilteredNavigationConfiguration>>();
	
	private static final String ENCODING = "UTF-8";
	
	private String separatorInTokenSettingKeyPath;
	
	private String separatorInToken;
	

	/**
	 * Loads FilteredNavigationConfiguration (FNC) for a Store represented by the given Store Code.
	 * 
	 * @param storeCode the code representing the Store for which FNC should be loaded
	 * @return the Filtered Navigation Configuration for the given store, or the default FNC
	 * if one cannot be found for the given store.
	 */
	public FilteredNavigationConfiguration loadFilteredNavigationConfiguration(final String storeCode) {
		// load the separator when we parse the first xml, to not take other separator that might have been updated
		// this method wasn't put in a spring init-method, due to the cyclic dependencies, that affects the loading
		// of the jpaPersistenceEngine.
		initSepartor();
		final String fncString = getFncString(storeCode);
		Pair<String, FilteredNavigationConfiguration> cachedValue = fncCache.get(storeCode);
		if (cachedValue == null || !fncString.equals(cachedValue.getFirst())) {
			FilteredNavigationConfiguration fnc = createConfigFromString(fncString);
			cachedValue = new Pair<String, FilteredNavigationConfiguration>(fncString, fnc);
			fncCache.put(storeCode, cachedValue);
		}
		return cachedValue.getSecond();
	}

	/**
	 * Initialize the separator when the first loading is happening.
	 * Do not reload the separator, because it will cause issue in advance search in SF.
	 */
	private void initSepartor() {
		// load the separatorToken
		if (separatorInToken != null) {
			return;
		}
		
		final SettingValue value = settingsReader.getSettingValue(getSeparatorInTokenSettingKeyPath());
		  
		if (value == null) {
			separatorInToken = SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN;
		} else {
			separatorInToken = value.getValue();
		}
				
	}
	/**
	 *
	 * @param fncString
	 * @return
	 */
	private FilteredNavigationConfiguration createConfigFromString(final String fncString) {
		FilteredNavigationConfiguration fnc = createFnc();
		InputStream configStream = getInputStreamFromString(fncString);
		parser.parse(configStream, fnc);
		return fnc;
	}
	
	/**
	 * Converts the given String into an InputStream.
	 * @param fncString the string from which to get an InputStream
	 * @return the inputStream
	 */
	InputStream getInputStreamFromString(final String fncString) {
		try {
			return new ByteArrayInputStream(fncString.getBytes(ENCODING));
		} catch (UnsupportedEncodingException ex) {
			LOG.error(ENCODING + "encoding is not supported by this platform", ex);
			throw new EpServiceException("Exception occurred that should never happen", ex);
		}
	}
	
	/**
	 * Retrieves the FilteredNavigation configuration as a String from the settings service.
	 * @param storeCode the code representing the store for which the FNC should be retrieved
	 * @return the FNC XML document as a String.
	 */
	String getFncString(final String storeCode) {
		return settingsReader.getSettingValue(getSettingKeyPath(), storeCode).getValue();
	}
	
	/**
	 * Creates a new FNC object using the bean factory.
	 * @return the new FNC object
	 */
	FilteredNavigationConfiguration createFnc() {
		return beanFactory.getBean("filteredNavigationConfiguration");
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the parser
	 */
	public FilteredNavigationConfigurationParser getParser() {
		return parser;
	}

	/**
	 * @param parser the parser to set
	 */
	public void setParser(final FilteredNavigationConfigurationParser parser) {
		this.parser = parser;
	}

	/**
	 * @param settingKeyPath the settingKeyPath to set
	 */
	public void setSettingKeyPath(final String settingKeyPath) {
		this.settingKeyPath = settingKeyPath;
	}

	/**
	 * @return the settingKeyPath
	 */
	public String getSettingKeyPath() {
		return settingKeyPath;
	}

	/**
	 * Sets the {@link SettingsReader} instance to use.
	 * 
	 * @param settingsReader {@link SettingsReader} instance to use
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	
	
	/**
	 * Getter for the setting path.
	 * @return the inFieldSeparatorSettingKeyPath
	 */
	public String getSeparatorInTokenSettingKeyPath() {
		return separatorInTokenSettingKeyPath;
	}

	/**
	 * Setter for the setting path property.
	 * @param inFieldSeparatorSettingKeyPath the inFieldSeparatorSettingKeyPath to set
	 */
	public void setSeparatorInTokenSettingKeyPath(final String inFieldSeparatorSettingKeyPath) {
		this.separatorInTokenSettingKeyPath = inFieldSeparatorSettingKeyPath;
	}

	@Override
	public String getSeparatorInToken() {
		// call here for the case in which the method loadFilteredNavigationConfiguration was not called first
		initSepartor();
		return separatorInToken;
	}
	
	
}
