package com.elasticpath.service.asset;

import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.asset.ImageMapWithAbsolutePath;

/**
 * Service for finding images for an object.
 *
 * @param <T> the generic type of the object to find images for
 */
public interface ImageDiscoveryService<T> {

	/**
	 * Gets the image map.
	 *
	 * @param catalogObject the catalog object to find images for
	 * @return the image map
	 */
	ImageMap getImageMap(T catalogObject);
	
	/**
	 * Gets the image map by code.
	 *
	 * @param catalogObjectCode the code of the catalog object to find images for
	 * @return the image map
	 */
	ImageMap getImageMapByCode(String catalogObjectCode);
	
	/**
	 * Set the paths on an {@link ImageMap} to be absolute paths.
	 * 
	 * @param imageMap the image map to enhance
	 * @param storeCode the code of the store to use in determining paths
	 * @return an image map with absolute paths
	 */
	ImageMapWithAbsolutePath absolutePathsForImageMap(ImageMap imageMap, String storeCode);
}
