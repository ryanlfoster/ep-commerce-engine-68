/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.SearchConfigInternal;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.SearchHostLocator;

/**
 * A factory for getting the default <code>SearchConfig</code>.
 */
public class DefaultSearchConfigFactoryImpl implements SearchConfigFactory {

	private BeanFactory beanFactory;

	private SearchHostLocator hostLocator;

	/**
	 * Gets the default search configuration regardless of the given <code>accessKey</code>.
	 * 
	 * @param accessKey is ignored
	 * @return a search configuration
	 */
	public SearchConfig getSearchConfig(final String accessKey) {
		SearchConfigInternal config = getBeanFactory().getBean(ContextIdNames.SEARCH_CONFIG);
		config.setSearchHost(hostLocator.getSearchHostLocation());
		return config;
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
