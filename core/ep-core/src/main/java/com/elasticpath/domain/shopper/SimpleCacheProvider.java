package com.elasticpath.domain.shopper;

import com.elasticpath.commons.util.SimpleCache;

/**
 * Provides access to an attribute cache. 
 */
public interface SimpleCacheProvider {

	/**
	 * Gets the {@link SimpleCache} that is implemented by this object.
	 *
	 * @return an Attribute cache.
	 */
	SimpleCache getCache();
	
}
