package com.elasticpath.domain.misc.impl;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.ImageRenderRequest;

/**
 * A default implementation of <code>ImageRenderRequest</code>.
 */
public class ImageRenderRequestImpl extends AbstractEpDomainImpl implements ImageRenderRequest {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String imageName;

	private String imageFilePath;

	private int requiredWidth;

	private int requiredHeight;

	private int padding;

	/**
	 * Return the image name.
	 *
	 * @return the image name
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Sets the image name.
	 *
	 * @param imageName the image name
	 */
	public void setImageName(final String imageName) {
		this.imageName = imageName;
	}

	/**
	 * Returns the full path to the image file that is to be rendered.
	 *
	 * @return the image file path
	 */
	public String getImageFilePath() {
		return imageFilePath;
	}

	/**
	 * Sets the full path to the image that is to be rendered.
	 *
	 * @param imageFilePath the image file path
	 */
	public void setImageFilePath(final String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}

	/**
	 * Sets the full path to the image that is to be rendered.
	 *
	 * @param imageFilePath the image file path
	 * @deprecated call {@link #setImageFilPath(String)}
	 */
	@Deprecated
	public void setImageFielPath(final String imageFilePath) {
		setImageFilePath(imageFilePath);
	}

	/**
	 * Returns the required width.
	 *
	 * @return the required width
	 */
	public int getRequiredWidth() {
		return requiredWidth;
	}

	/**
	 * Sets the required width.
	 *
	 * @param requiredWidth the required width
	 */
	public void setRequiredWidth(final int requiredWidth) {
		this.requiredWidth = requiredWidth;
	}

	/**
	 * Returns the required height.
	 *
	 * @return the required height
	 */
	public int getRequiredHeight() {
		return requiredHeight;
	}

	/**
	 * Sets the required height.
	 *
	 * @param requiredHeight the required height
	 */
	public void setRequiredHeight(final int requiredHeight) {
		this.requiredHeight = requiredHeight;
	}

	/**
	 * Returns the padding.
	 *
	 * @return the padding
	 */
	public int getPadding() {
		return padding;
	}

	/**
	 * Sets the padding.
	 *
	 * @param padding the padding
	 */
	public void setPadding(final int padding) {
		this.padding = padding;
	}
}
