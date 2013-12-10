/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.impl;

import com.elasticpath.domain.misc.SearchConfig;

/**
 * A factory for getting the <code>SearchConfig</code> for the Commerce Manager.
 */
public class CMSearchConfigFactoryImpl extends AbstractSettingsSearchConfigFactory {

	/**
	 * Gets the Commerce Manager specific search configuration for the given search index name.
	 * If no configuration is found for the given access key, returns the default configuration.
	 * 
	 * @param indexName the name of the index whose search configuration should be retrieved
	 * @return a search configuration
	 */
	public SearchConfig getSearchConfig(final String indexName) {
		final String context = "APPSPECIFIC/RCP/" + indexName;
		return getSearchConfig(indexName, context);
	}

}
