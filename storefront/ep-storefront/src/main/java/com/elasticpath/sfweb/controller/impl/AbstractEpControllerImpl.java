/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import org.springframework.web.servlet.mvc.BaseCommandController;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.sfweb.controller.EpController;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * <code>AbstractEpControllerImpl</code> is the base implementation of controller
 * in an Spring MVC pattern dealing with non-form based requests.
 */
public abstract class AbstractEpControllerImpl extends BaseCommandController implements EpController {

	private SfRequestHelper requestHelper;

	private BeanFactory beanFactory;

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the current requestHelper instance.
	 */
	public SfRequestHelper getRequestHelper() {
		return this.requestHelper;
	}

	/**
	 * Return a bean specified by the beanName from the bean factory.
	 *
	 * @param <T> generic to allow castless assignment
	 * @param beanName the name of the bean to return.
	 * @return the bean, or null if it's not found.
	 */
	protected <T> T getBean(final String beanName) {
		return beanFactory.getBean(beanName);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
