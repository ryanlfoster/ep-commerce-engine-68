package com.elasticpath.domain.asset;

import java.net.URL;

/**
 * Methods for setting information on a {@link ImageMapWithAbsolutePath}.
 */
public interface MutableImageMapWithAbsolutePath extends ImageMapWithAbsolutePath {
	
	/**
	 * Sets the path prefix.
	 *
	 * @param prefix the new path prefix
	 */
	void setPathPrefix(URL prefix);
	
	/**
	 * Sets the relative image map.
	 *
	 * @param imageMap the new relative image map
	 */
	void setRelativeImageMap(ImageMap imageMap);
	
}
