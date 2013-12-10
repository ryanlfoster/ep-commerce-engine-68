/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.controller;

import org.springframework.web.servlet.mvc.Controller;

import com.elasticpath.cmweb.util.CmRequestHelper;

/**
 * <code>EpController</code> represents a controller in an MVC pattern
 * dealing with non-form based requests.
 */
public interface EpController extends Controller {

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	void setRequestHelper(final CmRequestHelper requestHelper);

	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the currenct requestHelper instance.
	 */
	CmRequestHelper getRequestHelper();
}
