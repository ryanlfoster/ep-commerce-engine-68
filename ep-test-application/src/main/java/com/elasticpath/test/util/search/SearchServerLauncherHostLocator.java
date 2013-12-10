/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.util.search;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.service.search.SearchHostLocator;

/**
 * Implementation of {@link SearchHostLocator} that delegates to {@link SearchServerLauncher}.  
 */
public class SearchServerLauncherHostLocator implements SearchHostLocator {

	@Autowired
	private SearchServerLauncher searchServerLauncher;
	
	@Override
	public String getSearchHostLocation() {
		return searchServerLauncher.getSearchHostUrl();
	}
}
