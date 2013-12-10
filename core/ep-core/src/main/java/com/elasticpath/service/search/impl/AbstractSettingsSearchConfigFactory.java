/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.SearchConfigUtils;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.SearchConfigInternal;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.SearchHostLocator;
import com.elasticpath.settings.SettingsReader;

/**
 * Abstract class that uses the <code>SettingsService</code> to get a search configuration.
 */
public abstract class AbstractSettingsSearchConfigFactory implements SearchConfigFactory {

	private SettingsReader settingsReader;
	
	private BeanFactory beanFactory;

	private SearchHostLocator hostLocator;
	
	/**
	 * Get the search configuration for the given search index name and settings context.
	 * 
	 * @param indexName the name of the index whose search configuration should be retrieved
	 * @param settingsContext the context to use for getting a setting value
	 * @return a search configuration
	 */
	public SearchConfig getSearchConfig(final String indexName, final String settingsContext) {
		SearchConfigInternal searchConfig = getBeanFactory().getBean(ContextIdNames.SEARCH_CONFIG);
		
		searchConfig.setSearchHost(hostLocator.getSearchHostLocation());
		
		searchConfig.setMaxReturnNumber(Integer.parseInt(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/maxReturnNumber", settingsContext).getValue()));
		searchConfig.setMinimumSimilarity(Float.parseFloat(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/minimumSimilarity", settingsContext).getValue()));
		searchConfig.setPrefixLength(Integer.parseInt(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/prefixLength", settingsContext).getValue()));
		searchConfig.setMinimumResultsThreshold(Integer.parseInt(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/minimumResultsThreshold", settingsContext).getValue()));
		searchConfig.setMaximumResultsThreshold(Integer.parseInt(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/maximumResultsThreshold", settingsContext).getValue()));
		searchConfig.setMaximumSuggestionsPerWord(Integer.parseInt(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/maximumSuggestionsPerWord", settingsContext).getValue()));
		searchConfig.setAccuracy(Float.parseFloat(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/accuracy", settingsContext).getValue()));
		searchConfig.setBoostValues(SearchConfigUtils.boostMapFromString(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/boosts", settingsContext).getValue()));
		searchConfig.setExclusiveAttributes(SearchConfigUtils.attributeExclusionSetFromString(settingsReader.getSettingValue(
				"COMMERCE/SEARCH/excludeAttributes", settingsContext).getValue()));
		return searchConfig;
	}
	
	/**
	 * Get the settings reader.
	 * 
	 * @return the <code>SettingsReader</code>
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the settings service.
	 * 
	 * @param settingsReader the settingsService to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Get the bean factory.
	 * 
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * Set the bean factory.
	 * 
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Set the locator providing the search host URL string.
	 * @param locator instance to use
	 */
	public void setSearchHostLocator(final SearchHostLocator locator) {
		this.hostLocator = locator;
	}

}
