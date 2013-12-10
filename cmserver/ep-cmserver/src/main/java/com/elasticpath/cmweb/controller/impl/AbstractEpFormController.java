package com.elasticpath.cmweb.controller.impl;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.elasticpath.cmweb.controller.EpController;
import com.elasticpath.cmweb.util.CmRequestHelper;

/**
 * <code>AbstactEpFormControllerImpl</code> represents a controller in an Spring MVC
 * pattern dealing with form based requests.
 */
public abstract class AbstractEpFormController extends SimpleFormController implements EpController {
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
