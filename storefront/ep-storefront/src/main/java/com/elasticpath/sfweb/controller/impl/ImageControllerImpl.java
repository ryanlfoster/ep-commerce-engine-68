/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.service.misc.ImageService;
import com.elasticpath.web.controller.impl.ImageControllerHelper;

/**
 * The Spring MVC controller for image re-sizing.
 */
public class ImageControllerImpl extends AbstractEpControllerImpl {

	private ImageControllerHelper imageControllerHelper;

	private ImageService imageService;
	
	/**
	 * Return the ModelAndView for the configured static view page.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return getImageControllerHelper().handleRequest(request, response, getRequestHelper(), getImageService().getImagePath());
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
