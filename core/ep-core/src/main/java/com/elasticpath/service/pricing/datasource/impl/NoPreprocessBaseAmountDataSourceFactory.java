package com.elasticpath.service.pricing.datasource.impl;

import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;

/**
 * A {@link BaseAmountDataSourceFactory} that does no pre-processing on the data, and simply returns 
 * the backingDataSource as the created {@link BaseAmountDataSource}.
 */
public class NoPreprocessBaseAmountDataSourceFactory implements BaseAmountDataSourceFactory {

	@Override
	public BaseAmountDataSource createDataSource(final BaseAmountDataSource backingDataSource) {
		return backingDataSource;
	}
	
}
