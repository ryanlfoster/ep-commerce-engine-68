/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.search.index.solr;


/**
 * This interface is implemented by classes who wish to be
 * informed of events from an index build service.
 */
public interface IndexBuildEventListener {

	/**
	 * This method is called when an index build is completed.
	 */
	void indexBuildComplete();
	
}
