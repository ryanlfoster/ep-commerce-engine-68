package com.elasticpath.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Provides the ability to send file contents to a browser.
 */
public interface FileDownloadService {

	/**
	 * Read the input file content and output the stream to client (Browser).
	 *
	 * @param request the download request
	 * @param response the response
	 * @param fullFilePath the full file path on server
	 * @throws EpSystemException - if any error
	 *
	 */
	void download(HttpServletRequest request, HttpServletResponse response, String fullFilePath) throws EpSystemException;

}
