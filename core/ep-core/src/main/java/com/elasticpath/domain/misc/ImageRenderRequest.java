package com.elasticpath.domain.misc;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a request to render a image.
 */
public interface ImageRenderRequest extends EpDomain {
	/**
	 * Return the image name.
	 *
	 * @return the image name
	 */
	String getImageName();

	/**
	 * Sets the image name.
	 *
	 * @param imageName the image name
	 */
	void setImageName(String imageName);

	/**
	 * Returns the full path to the image file that is to be rendered.
	 *
	 * @return the path
	 */
	String getImageFilePath();

	/**
	 * Sets the full path to the image file that is to be rendered.
	 *
	 * @param imageFilePath the image file path
	 *
	 */
	void setImageFilePath(String imageFilePath);

	/**
	 * Sets the full path to the image file that is to be rendered.
	 *
	 * @param imageFilePath the image file path
	 * @deprecated call {{@link #setImageFilePath(String)}
	 */
	@Deprecated
	void setImageFielPath(String imageFilePath);

	/**
	 * Returns the required width.
	 *
	 * @return the required width
	 */
	int getRequiredWidth();

	/**
	 * Sets the required width.
	 *
	 * @param requiredWidth the required width
	 */
	void setRequiredWidth(int requiredWidth);

	/**
	 * Returns the required height.
	 *
	 * @return the required height
	 */
	int getRequiredHeight();

	/**
	 * Sets the required height.
	 *
	 * @param requiredHeight the required height
	 */
	void setRequiredHeight(int requiredHeight);

	/**
	 * Returns the padding.
	 *
	 * @return the padding
	 */
	int getPadding();

	/**
	 * Sets the padding.
	 *
	 * @param padding the padding
	 */
	void setPadding(int padding);

}
