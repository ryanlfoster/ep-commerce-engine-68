/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service;

import com.elasticpath.domain.ElasticPath;



/**
 * Provides the ability to access elastic path context.
 */
public interface ElasticPathService {
	/**
	 * Return the elastic path context.
	 * @return the elastic path context
	 */
	ElasticPath getElasticPath();

	/**
	 * Initialize the elastic path context.
	 */
	void init();

	/**
	 * Return the store front context URL.
	 * 
	 * @return the store front context URL
	 */
	String getStorefrontContextUrl();
}
