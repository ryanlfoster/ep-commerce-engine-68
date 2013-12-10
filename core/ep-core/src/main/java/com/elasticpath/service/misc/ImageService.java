package com.elasticpath.service.misc;

import com.elasticpath.domain.misc.ImageRenderRequest;
import com.elasticpath.domain.misc.ImageRenderResponse;
import com.elasticpath.service.EpService;

/**
 * Provides image rendering service.
 */
public interface ImageService extends EpService {
	/**
	 * Render a image based on the given request and returns a <code>ImageRenderResponse</code>.
	 * 
	 * @param request the image render request
	 * @return a <code>ImageRenderResponse</code>
	 */
	ImageRenderResponse render(ImageRenderRequest request);
	
	/**
	 * Gets the full path prefix to image files.
	 * @return the full path prefix to image files
	 */
	String getImagePath();
	
	/**
	 * Gets the full path prefix to image files.
	 *
	 * @param subFolder the sub folder
	 * @return the full path prefix to image files
	 */
	String getImagePath(String subFolder);
	
	/**
	 * Returns the jpeg quality to use when resizing a jpeg in the dynamic image resizing engine.
	 * 
	 * @return the jpeg quality factor
	 */
	float getJPEGQuality();
}
