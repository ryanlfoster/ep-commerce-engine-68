package com.elasticpath.web.controller.impl;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.media.jai.JAI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.ImageRenderRequest;
import com.elasticpath.domain.misc.ImageRenderResponse;
import com.elasticpath.service.misc.ImageService;
import com.elasticpath.web.security.EsapiServletUtils;
import com.elasticpath.web.util.RequestHelper;
import com.sun.media.jai.codec.JPEGEncodeParam;

/**
 * Enables image controllers in both SF and CM.
 */
public class ImageControllerHelper implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ImageControllerHelper.class);
	private static final String IMAGE_NAME_PARAMETER_STR = "imageName";

	private ImageService imageService;

	private BeanFactory beanFactory;
	
	/**
	 * Return the ModelAndView for the configured static view page.
	 * Uses ESAPI Servlet Utils to wrap the request and response to protect against header manipulation.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @param requestHelper the request helper
	 * @param imagePathPrefix the full path folder the image is kept in
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response, 
			final RequestHelper requestHelper, final String imagePathPrefix) throws Exception {
		
		safeRenderImage(EsapiServletUtils.secureHttpRequest(request), EsapiServletUtils.secureHttpResponse(response),
				requestHelper, imagePathPrefix);

		return null;
	}

	/**
	 * Safely render the image by only accepting OWASP ESAPI wrappers of the request & response.
	 *
	 * @param safeRequest the request wrapped by <code>SecurityWrapperRequest</code>
	 * @param safeResponse the response wrapped by <code>SecurityWrapperResponse</code>
	 * @param requestHelper the request helper
	 * @param imagePathPrefix the full path folder the image is kept in
	 * @throws IOException if there is an exception during rendering
	 */
	protected void safeRenderImage(final HttpServletRequest safeRequest, final HttpServletResponse safeResponse,
			final RequestHelper requestHelper, final String imagePathPrefix) throws IOException {
		final String imageName = getImageFileName(safeRequest);
		final String filePath = imagePathPrefix + File.separator + imageName;
		
		final int requiredWidth = requestHelper.getIntParameterOrAttribute(safeRequest, "width", 0);
		final int requiredHeight = requestHelper.getIntParameterOrAttribute(safeRequest, "height", 0);
		final int padding = requestHelper.getIntParameterOrAttribute(safeRequest, "padding", 0);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Supplied Imagename is: " + imageName);
		}

		// Compose a image render request
		final ImageRenderRequest imageRenderRequest = getBeanFactory().getBean(ContextIdNames.IMAGE_RENDER_REQUEST);
		imageRenderRequest.setImageName(imageName);
		imageRenderRequest.setImageFilePath(filePath);
		imageRenderRequest.setRequiredHeight(requiredHeight);
		imageRenderRequest.setRequiredWidth(requiredWidth);
		imageRenderRequest.setPadding(padding);

		final ImageRenderResponse imageRenderResponse = imageService.render(imageRenderRequest);

		// Set the response content type
		safeResponse.setContentType(imageRenderResponse.getMimeType());
		
		int fileNameStart = imageRenderResponse.getFileName().lastIndexOf(File.separator);
		String fileNameOnly = imageRenderResponse.getFileName().substring(fileNameStart + 1);
		safeResponse.setHeader("Content-disposition", "inline; filename=" + fileNameOnly);

		// Encode the image
		encodeImage(safeResponse, imageRenderResponse.getScaledImage(), imageRenderResponse.getImageType());
	}

	/**
	 * Gets the image file name.
	 *
	 * @param safeRequest the safe request
	 * @return the image file name
	 */
	private String getImageFileName(final HttpServletRequest safeRequest) {
		String fileName = safeRequest.getParameter(IMAGE_NAME_PARAMETER_STR);
		return scrubFilename(fileName);
	}

	/**
	 * Scrubs the given filename by replacing escaped HTML with the actual characters and normalizing the path.
	 * This implementation uses {@link StringEscapeUtils#unescapeHtml(String)} and
	 * {@link FilenameUtils#normalize(String)}.
	 * @param filename the filename or path to be scrubbed
	 * @return the scrubbed filename
	 */
	protected String scrubFilename(final String filename) {
		return FilenameUtils.normalize(StringEscapeUtils.unescapeHtml(filename));
	}
	
	/**
	 * Encode the given image data using JAI.
	 * 
	 * @param response the response to send out the encoded image data
	 * @param scaledImage the image to encode
	 * @param imageType the type of image
	 * @throws IOException in case of an encoding error
	 */
	protected void encodeImage(
			final HttpServletResponse response, final RenderedImage scaledImage, final String imageType) throws IOException {
		
		long start = new Date().getTime();

		String encodeImageType = imageType;
		JPEGEncodeParam param = null;
		if (imageType.equalsIgnoreCase("jpeg")) {
			param = new JPEGEncodeParam();
			param.setQuality(getResizedImageQualityFactor());
		}

		// JAI does not support encoding GIF's. When image type is a GIF encode as a png.
		if (encodeImageType.equalsIgnoreCase("gif")) {
			encodeImageType = "png";
		}

		JAI.create("encode", scaledImage, response.getOutputStream(), encodeImageType, param);

		if (LOG.isDebugEnabled()) {
			long end = new Date().getTime();
			LOG.debug("Image Encode Time (ms): " + new Long(end - start));
		}
	}

	/**
	 * @return the quality factor to use when resizing an image in the dynamic image resizing engine.
	 */
	protected float getResizedImageQualityFactor() {
		return imageService.getJPEGQuality();
	}
	
	/**
	 * Sets the image service.
	 *
	 * @param imageService the image service
	 */
	public void setImageService(final ImageService imageService) {
		this.imageService = imageService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

}
