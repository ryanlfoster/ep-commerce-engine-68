package com.elasticpath.service.misc.impl;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.misc.ImageRenderRequest;
import com.elasticpath.domain.misc.ImageRenderResponse;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.ImageService;
import com.elasticpath.settings.SettingsReader;
import com.sun.media.jai.codec.ImageCodec;

/**
 * Provides image rendering service.
 *
 * <p>For the terrible bug [MSC-3493].
 * That's a bug about JAI, which was said to be solved in JAI1.1.3 Beta. But it is still there for some condition.
 * See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6331420
 * When we resize an image from 200W/150H to 176W/101H, we still get the black border.
 *
 * Solution is:
 * Step1: resize the image from 200W/150H to 176+2W/101+2H.
 * Step2: crop the 176+2W/101+2H image to 176W/101H with cut 1pixel on each side.
 * Step3: add borders to fill up the whole image.
 * </p>
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ImageServiceImpl extends AbstractEpServiceImpl implements ImageService {
	private static final Logger LOG = Logger.getLogger(ImageServiceImpl.class);

	private static final int BORDER_FILL = 255;
	
	private static final float DEFAULT_JPG_QUALITY = 0.5f;

	private SettingsReader settingsReader;
	
	private AssetRepository assetRepository;
	
	private static final Map<String, String> MIME_TYPE_MAP = new HashMap<String, String>();
	
	static {
		MIME_TYPE_MAP.put("gif", "image/gif");
		MIME_TYPE_MAP.put("jpg", "image/jpg");
		MIME_TYPE_MAP.put("jpeg", "image/jpg");
		MIME_TYPE_MAP.put("bmp", "image/bmp");
		MIME_TYPE_MAP.put("png", "image/png");
		MIME_TYPE_MAP.put("tiff", "image/tiff");
	}

	/**
	 * Render a image based on the given request and returns a <code>ImageRenderResponse</code>.
	 * 
	 * In this implementation, if the image with the filename specified in the request cannot be found
	 * then the rendered image will be that with the filename equal to the configured "noimage" fallback.
	 *
	 * @param request the image render request
	 * @return a <code>ImageRenderResponse</code>
	 * @throws com.elasticpath.base.exception.EpServiceException if there is a problem decoding the image file 
	 * (e.g. neither the image file specified in the request nor the default image file exist, 
	 * the image's extension is of a type not supported by the decoder, there is an IO problem).
	 */
	public ImageRenderResponse render(final ImageRenderRequest request) {

		File file = getFileToDecode(request.getImageFilePath());
		
		// Decode the image from the filesystem
		PlanarImage originalImage = decodeImage(file);

		// Scale the image to requested size
		PlanarImage scaledImage = scaleImage(originalImage, request.getRequiredHeight(), request.getRequiredWidth(), request.getPadding());

		// Release the memory on the originalImage
		originalImage.dispose();
		originalImage = null;

		ImageRenderResponse response = composeResponse(this.getImageExt(file.getName()), scaledImage);
		response.setFileName(file.getName());
		return response;
	}

	/**
	 * Creates an ImageRenderResponse with the given image type and scaled image.
	 * @param imageType the image extension
	 * @param scaledImage the scaled image
	 * @return the response
	 */
	ImageRenderResponse composeResponse(final String imageType,	final PlanarImage scaledImage) {
		// Compose a response
		final ImageRenderResponse response = getBean(ContextIdNames.IMAGE_RENDER_RESPONSE);
		response.setImageType(imageType);
		response.setScaledImage(scaledImage);
		response.setMimeType(mapExtToMime(imageType));
		return response;
	}

	/**
	 * Gets the image extension to be used for initializing the decoder, given a filename.
	 * @param imageName the file name of the image
	 * @return the string to be used for initializing the image decoder.
	 * @throws EpSystemException if the filename extension indicates that the file format is not compatible with
	 * image decoder.
	 */
	String getImageExt(final String imageName) {
		String ext = FilenameUtils.getExtension(imageName);
		if (StringUtils.isBlank(ext) || "jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
			return "jpeg";
		} else if ("tif".equalsIgnoreCase(ext) || "tiff".equalsIgnoreCase(ext)) {
			return "tiff";
		} else if ("gif".equalsIgnoreCase(ext)) {
			return ext;
		} else if ("bmp".equalsIgnoreCase(ext)) {
			return ext;
		} else if ("png".equalsIgnoreCase(ext)) {
			return ext;
		}
		throw new EpSystemException(ext + " images are not supported by the image resizer");
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private PlanarImage scaleImage(final PlanarImage originalImage, final int requiredHeight, final int requiredWidth, final int padding) {

		long start = new Date().getTime();
		final int roundScale = 10;

		PlanarImage scaledImage = null;
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();
		int requiredWidthWithPadding = requiredWidth - (padding * 2);
		int requiredHeightWithPadding = requiredHeight - (padding * 2);

		BigDecimal xScaleFactor = BigDecimal.ONE;
		BigDecimal yScaleFactor = BigDecimal.ONE;
		if (requiredWidthWithPadding != 0 && requiredWidthWithPadding < originalWidth) {
			xScaleFactor = new BigDecimal((double) requiredWidthWithPadding + 2).divide(new BigDecimal((double) originalWidth), roundScale,
					BigDecimal.ROUND_UP);
		}
		if (requiredHeightWithPadding != 0 && requiredHeightWithPadding < originalHeight) {
			yScaleFactor = new BigDecimal((double) requiredHeightWithPadding + 2).divide(new BigDecimal((double) originalHeight), roundScale,
					BigDecimal.ROUND_UP);
		}

		if (xScaleFactor.compareTo(yScaleFactor) == -1) {
			scaledImage = doJAIscaleImage(originalImage, xScaleFactor.doubleValue());
		} else if (xScaleFactor.compareTo(yScaleFactor) == 1) {
			scaledImage = doJAIscaleImage(originalImage, yScaleFactor.doubleValue());
		} else if (xScaleFactor.compareTo(yScaleFactor) == 0 && xScaleFactor.compareTo(BigDecimal.ONE) == 0) {
			scaledImage = originalImage;
		} else if (xScaleFactor.compareTo(yScaleFactor) == 0 && xScaleFactor.compareTo(BigDecimal.ZERO) == 1) {
			scaledImage = doJAIscaleImage(originalImage, yScaleFactor.doubleValue());
		} else {
			scaledImage = originalImage;
		}

		if (requiredWidth > scaledImage.getWidth() || requiredHeight > scaledImage.getHeight() || padding > 0) {
			BigDecimal horizontalPad = BigDecimal.ZERO;
			BigDecimal verticalPad = BigDecimal.ZERO;

			if (requiredWidth > scaledImage.getWidth()) {
				horizontalPad = new BigDecimal((double) (requiredWidth - scaledImage.getWidth())).divide(new BigDecimal("2"), 2, BigDecimal.ROUND_UP);
			}
			horizontalPad = horizontalPad.add(new BigDecimal((double) padding));

			if (requiredHeight > scaledImage.getHeight()) {
				verticalPad = new BigDecimal((double) (requiredHeight - scaledImage.getHeight())).divide(new BigDecimal("2"), 2, BigDecimal.ROUND_UP);
			}
			verticalPad = verticalPad.add(new BigDecimal((double) padding));

			scaledImage = doJAIborderImage(scaledImage, horizontalPad, verticalPad);
		}

		if (LOG.isDebugEnabled()) {
			long end = new Date().getTime();
			LOG.debug("Image Scale Time (ms): " + new Long(end - start));
		}

		return scaledImage;
	}

	private PlanarImage doJAIborderImage(final PlanarImage scaledImage, final BigDecimal horizonalPad, final BigDecimal verticalPad) {
		ParameterBlock params = new ParameterBlock();
		params.addSource(scaledImage);
		params.add((int) Math.ceil(horizonalPad.doubleValue())); // left pad (round up)
		params.add((int) Math.floor(horizonalPad.doubleValue())); // right pad (round down)
		params.add((int) Math.ceil(verticalPad.doubleValue())); // top pad (round up)
		params.add((int) Math.floor(verticalPad.doubleValue())); // bottom pad (round down)
		int numbands = scaledImage.getSampleModel().getNumBands();
		double[] fillValue = new double[numbands];
		for (int i = 0; i < numbands; i++) {
			fillValue[i] = BORDER_FILL;
		}
		params.add(new BorderExtenderConstant(fillValue)); // type
		params.add(fillValue); // fill
		return JAI.create("border", params);
	}

	private PlanarImage doJAIscaleImage(final PlanarImage originalImage, final double scaleFactor) {

		if (scaleFactor >= 1) { //Make sure the param is correctly passed in.
			return originalImage;
		}

		// Specify the rendering hints
		RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		ParameterBlock params = new ParameterBlock();
		params.addSource(originalImage);
		params.add(scaleFactor);

//		return JAI.create("SubsampleAverage", params, renderingHints);
		PlanarImage tmpImage = JAI.create("SubsampleAverage", params, renderingHints);

		ParameterBlock cropParams = new ParameterBlock();
		cropParams.addSource(tmpImage);
		cropParams.add((float) 1);
		cropParams.add((float) 1);
		cropParams.add((float) tmpImage.getWidth() - 2);
		cropParams.add((float) tmpImage.getHeight() - 2);
		PlanarImage retImage = JAI.create("crop", cropParams, null);
		tmpImage.dispose();

		return retImage;

	}
	
	/**
	 * Checks whether a given file exists; factored out for ease of testing.
	 * @param file the file to check
	 * @return true if the 1) file exists and 2) is not a directory and 3) is readable, false if otherwise. 
	 */
	boolean fileExists(final File file) {
		return file.exists() && file.isFile() && file.canRead();
	}
	
	/**
	 * Gets the file to decode from the file system. If the file with the given
	 * name doesn't exist, returns the default "no image" file.
	 * Calls {@link #getNoImageFilePath()}.
	 * @param filename the name of the file to decode
	 * @return the file to decode
	 */
	File getFileToDecode(final String filename) {
		File file = null;
		String normalizedFilename = normalizeFilename(filename);
			if (LOG.isDebugEnabled()) {
			LOG.debug("Requested Image Filename: " + filename);
			LOG.debug("Normalized Image Filename: " + normalizedFilename);
			}
		file = new File(normalizedFilename);
		if (!file.exists() || !file.isFile()) {
			LOG.debug("Requested normalized image file not found. Falling back to configured default image.");
			file = new File(getNoImageFilePath());
		}
		return file;
	}
	
	/**
	 * Normalizes a path, removing double and single dot path steps.
	 * This implementation uses Apache FilenameUtils.
	 * @param filename the filename to normalize
	 * @return the normalized filename
	 */
	String normalizeFilename(final String filename) {
		return FilenameUtils.normalize(filename);
	}
	
	/**
	 * Calls {@link #getImagePath()}, and {@link #getNoImageFileName()}.
	 * @return the configured path to the image file that is to be used when the requested image does not exist.
	 */
	String getNoImageFilePath() {
		return getImagePath() + File.separator + getNoImageFileName();
	}
	
	/**
	 * Calls {@link #getSettingsReader()} to retrieve the dynamicImageSizingNoImage setting value.
	 * @return the filename of the image to be returned when no image is available.
	 */
	String getNoImageFileName() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/IMAGES/dynamicImageSizingNoImage").getValue();
	}
	
	/**
	 * Reads in an image from the file system and converts it to a {@link PlanarImage}.
	 * @param file the file name of the image
	 * @return the image, as a PlanarImage
	 * @throws EpSystemException if the given file or the fallback image file cannot be found,
	 * or if there is a problem closing the input stream
	 */
	@SuppressWarnings("PMD.DoNotThrowExceptionInFinally")
	PlanarImage decodeImage(final File file) {

		long start = new Date().getTime();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Decode image file:" + file.getAbsolutePath());
		}

		RenderedImage renderedImg = null;
		try {
			renderedImg = ImageCodec.createImageDecoder(getImageExt(file.getName()), file, null).decodeAsRenderedImage();
		} catch (IOException e) {
			throw new EpSystemException("Exception creating image decoder for file " + file.getAbsolutePath(), e);
		}

		final PlanarImage load = new RenderedImageAdapter(renderedImg);

		long end = new Date().getTime();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Image Decode Time (ms): " + new Long(end - start));
		}

		return load;
	}

	/**
	 * Deciphers the mime type from the file extension.
	 * If the mime type is not known, will return "application/octet-stream"
	 * as the mime type.
	 * @param ext the file extension
	 * @return the mime type string
	 */
	String mapExtToMime(final String ext) {
		String mimeType = MIME_TYPE_MAP.get(ext);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		return mimeType;
	}
	
	/**
	 * Gets the full path prefix to image files.
	 * This implementation calls {@link #getSettingsReader()} to get the configured assetLocation
	 * and imageAssetsSubfolder.
	 * @return the full path prefix to image files
	 */
	public String getImagePath() {
		return getAssetRepository().getCatalogImagesPath();
	}
	
	/**
	 * Returns the jpeg quality to use when resizing a jpeg in the dynamic image resizing engine.
	 * If there is a problem retrieving the configured setting for any reason, a default quality of
	 * 0.5f will be used.
	 * 
	 * @return the jpeg quality factor
	 */
	public float getJPEGQuality() {
		float configuredJpegQuality = 0.0f;
		try {
			configuredJpegQuality = Float.valueOf(
				getSettingsReader().getSettingValue("COMMERCE/SYSTEM/IMAGES/dynamicImageSizingJpegQuality").getValue()
				);
		} catch (Exception ex) {
			LOG.error("There was a problem retrieving the dynamicImageSizingJpegQuality setting.", ex);
			return DEFAULT_JPG_QUALITY;
		}
		return configuredJpegQuality;
	}
	
	/**
	 * Sets the Settings Reader.
	 * @param settingsReader the settings reader
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Gets the Settings Reader.
	 * @return the settings reader
	 */
	public SettingsReader getSettingsReader() {
		return this.settingsReader;
	}

	/**
	 * @return the assetRepository
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	@Override
	public String getImagePath(final String subFolder) {
		if (StringUtils.isBlank(subFolder)) {
			return getImagePath();
		}
		return getImagePath() + File.separator + subFolder;
	}
}
