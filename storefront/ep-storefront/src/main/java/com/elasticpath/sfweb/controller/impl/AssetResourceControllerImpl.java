package com.elasticpath.sfweb.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.util.AssetResourceRetrievalStrategy;


/**
 * Resource resolving controller for store assets.
 * This controller needs to take retrieval strategies to resources in URL format according to own needs, and this class will return result to
 * response outputstream.
 *
 */
public class AssetResourceControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(AssetResourceControllerImpl.class);

	private AssetResourceRetrievalStrategy resourceRetrievalStrategy;

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		String requestedResourcePath = getRequestedResourcePath(request);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Requested Resource Path: " + requestedResourcePath);
		}

		if (getResourceRetrievalStrategy() == null) {
			throw new EpSfWebException("Resource retrieval strategy not set");
		}
		URL requestedResourceUrl = getResourceRetrievalStrategy().resolveResource(requestedResourcePath);
		if (requestedResourceUrl == null) {
			LOG.error("Exception returning requested resource: " + requestedResourcePath);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		int fileNameStart = requestedResourceUrl.getPath().lastIndexOf("/");
		String fileNameOnly = requestedResourceUrl.getPath().substring(fileNameStart + 1);
		response.setHeader("Content-disposition", "inline; filename=" + fileNameOnly);

		copyResourceIntoResponse(response, requestedResourceUrl);

		return null;
	}

	/**
	 * Copies the resource from the URL to the response and sets the mimetype header.
	 * @param response The response to copy to.
	 * @param requestedResourceUrl The url to copy from.
	 * @throws IOException If an exception is thrown.
	 */
	void copyResourceIntoResponse(final HttpServletResponse response,
			final URL requestedResourceUrl) throws IOException {
		InputStream inputStream = requestedResourceUrl.openStream();
		response.setContentType(getMimeType(new File(requestedResourceUrl.getFile())));

		try {
			Streams.copy(inputStream, response.getOutputStream(), true);
		} catch (Exception exception) {
			final int levelsToScan = 3;
			if (isSocketException(exception, levelsToScan)) {
				LOG.warn("SocketException thrown during copy of asset "
						+ requestedResourceUrl + " to response output stream. Likely due to spurious connection reset.");
			} else {
				LOG.error(exception.getClass().getName() + "thrown during copy of asset "
						+ requestedResourceUrl + " to response output stream.", exception);
			}
		}
	}

	/**
	 * This checks that the current exception or its cause is a SocketException.
	 * This check is required in case when flash files are streamed out.
	 * The reason for deep scanning is that servlet containers such as Tomcat
	 * for example wraps the SocketException by a Catalina exception.
	 * @param throwable the exception
	 * @param levelsToScan how many level deep to scan (using getCause)
	 * @return true if current exception or its cause (up to levelToScan) is
	 * instance of SocketException
	 */
	boolean isSocketException(final Throwable throwable, final int levelsToScan) {
		if (throwable == null) {
			return false;
		}
		if (levelsToScan >= 0) {
			if (throwable instanceof java.net.SocketException) {
				return true;
			}
			return isSocketException(throwable.getCause(), levelsToScan - 1);
		}
		return false;
	}

	/**
	 * Get the asset resource retrieval strategy.
	 *
	 * @return strategy to be used in retrieving a resource.
	 */
	public AssetResourceRetrievalStrategy getResourceRetrievalStrategy() {
		return resourceRetrievalStrategy;
	}


	/**
	 * Set the asset resource retrieval strategy.
	 *
	 * @param strategy to be used in retrieving a resource.
	 */
	public void setResourceRetrievalStrategy(final AssetResourceRetrievalStrategy strategy) {
		this.resourceRetrievalStrategy = strategy;
	}

	/**
	 * Extract the resource location from the request.
	 *
	 * @param request the request for the resource.
	 * @return the location of the resource prefixed with a '/' without the
	 *         servlet context at its start.
	 */
	String getRequestedResourcePath(final HttpServletRequest request) {
		final String requestUri = request.getRequestURI();
		final String contextPath = request.getContextPath();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Request URI: " + requestUri);
			LOG.debug("Context Path: " + contextPath);
		}

		// Not using getPathInfo() because we were seeing null's on WebLogic
		// Substring will always leave the preceding slash as the start of the requested path.
		return requestUri.substring(contextPath.length());
	}


	/**
	 * Get the mime type of a given file.
	 * @param resource the file whose MIME type is required
	 * @return the mime type of the file, or
	 */
	String getMimeType(final File resource) {
		ServletContext context = getServletContext();
		String mimeType = context.getMimeType(resource.getName());
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		return mimeType;
	}

}
