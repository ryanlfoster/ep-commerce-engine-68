/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.controller.impl;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.service.misc.ImageService;
import com.elasticpath.web.controller.impl.ImageControllerHelper;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * The Spring MVC controller for image re-sizing.
 */
public class ImageControllerImpl extends AbstractEpControllerImpl {

	private ImageControllerHelper imageControllerHelper;
	private ImageService imageService;
	

	private static final String SUB_FOLDER_PARAMETER_STR = "subFolder";

	/**
	 * Return the ModelAndView for the configured static view page.
	 * Uses ESAPI Servlet Utils to protect against header manipulation.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final String subFolder = EsapiServletUtils.secureHttpRequest(request).getParameter(SUB_FOLDER_PARAMETER_STR);
		return getImageControllerHelper().handleRequest(request, response, getRequestHelper(), 
				getPathPrefixForSubFolder(subFolder));
	}

	/**
	 * Gets the path prefix given the sub folder. 
	 * If a non-blank sub folder is specified, the path will be absolutePathPrefix/subFolder.
	 * For a blank sub folder, the path will be absolutePathPrefix.
	 * 
	 * @param subFolder the sub folder
	 * @return the path prefix for sub folder
	 */
	protected String getPathPrefixForSubFolder(final String subFolder) {
		String imagePath = getImageService().getImagePath();
		if (StringUtils.isBlank(subFolder)) {
			return imagePath;
		}
		return imagePath + File.separator + subFolder;
	}

	public void setImageService(final ImageService imageService) {
		this.imageService = imageService;
	}

	protected ImageService getImageService() {
		return imageService;
	}

	public void setImageControllerHelper(final ImageControllerHelper imageControllerHelper) {
		this.imageControllerHelper = imageControllerHelper;
	}

	protected ImageControllerHelper getImageControllerHelper() {
		return imageControllerHelper;
	}
}
