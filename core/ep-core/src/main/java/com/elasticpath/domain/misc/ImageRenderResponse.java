package com.elasticpath.domain.misc;

import java.awt.image.RenderedImage;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a response to render a image.
 */
public interface ImageRenderResponse extends EpDomain {
	/**
	 * Return the image type.
	 *
	 * @return the image type
	 */
	String getImageType();

	/**
	 * Sets the image type.
	 *
	 * @param imageType the image type
	 */
	void setImageType(String imageType);

	/**
	 * Returns the scaled image.
	 *
	 * @return the scaled image
	 */
	RenderedImage getScaledImage();

	/**
	 * Sets the scaled image.
	 *
	 * @param scaledImage the scaled image
	 */
	void setScaledImage(RenderedImage scaledImage);

	/**
	 * Return the mime type.
	 *
	 * @return the mime type
	 */
	String getMimeType();

	/**
	 * Sets the mime type.
	 *
	 * @param mimeType the mime type
	 */
	void setMimeType(String mimeType);

	/**
	 * Gets the actual file name.
	 *
	 * @return the fileName
	 */
	String getFileName();

	/**
	 * Sets the actual filename.
	 *
	 * @param fileName the fileName to set
	 */
	void setFileName(final String fileName);

}
