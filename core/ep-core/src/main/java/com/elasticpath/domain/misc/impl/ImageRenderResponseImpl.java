package com.elasticpath.domain.misc.impl;

import java.awt.image.RenderedImage;
import java.io.IOException;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.ImageRenderResponse;

/**
 * Represents a response to render a image.
 */
public class ImageRenderResponseImpl extends AbstractEpDomainImpl implements ImageRenderResponse {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String imageType;

	private transient RenderedImage scaledImage;

	private String mimeType;

	private String fileName;

	/**
	 * Return the image type.
	 *
	 * @return the image type
	 */
	public String getImageType() {
		return imageType;
	}

	/**
	 * Sets the image type.
	 *
	 * @param imageType the image type
	 */
	public void setImageType(final String imageType) {
		this.imageType = imageType;
	}

	/**
	 * Returns the scaled image.
	 *
	 * @return the scaled image
	 */
	public RenderedImage getScaledImage() {
		return scaledImage;
	}

	/**
	 * Sets the scaled image.
	 *
	 * @param scaledImage the scaled image
	 */
	public void setScaledImage(final RenderedImage scaledImage) {
		this.scaledImage = scaledImage;
	}

	/**
	 * Return the mime type.
	 *
	 * @return the mime type
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the mime type.
	 *
	 * @param mimeType the mime type
	 */
	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	private void readObject(final java.io.ObjectInputStream aInputStream) throws IOException, ClassNotFoundException {
		// always perform the default de-serialization first
		aInputStream.defaultReadObject();
		throw new EpDomainException("This object shouldnot be serialized.");
	}

	/**
	 * Gets the actual file name.
	 *
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the actual filename.
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}
}
