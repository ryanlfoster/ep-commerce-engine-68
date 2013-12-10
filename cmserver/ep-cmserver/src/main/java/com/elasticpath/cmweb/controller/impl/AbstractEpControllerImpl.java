/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.controller.impl;

import org.springframework.web.servlet.mvc.BaseCommandController;

import com.elasticpath.cmweb.controller.EpController;
import com.elasticpath.cmweb.util.CmRequestHelper;

/**
 * <code>AbstractEpControllerImpl</code> is the base implementation of controller
 * in an Spring MVC pattern dealing with non-form based requests.
 */
public abstract class AbstractEpControllerImpl extends BaseCommandController implements EpController {

	private CmRequestHelper requestHelper;

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final CmRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the currenct requestHelper instance.
	 */
	public CmRequestHelper getRequestHelper() {
		return this.requestHelper;
	}
}
