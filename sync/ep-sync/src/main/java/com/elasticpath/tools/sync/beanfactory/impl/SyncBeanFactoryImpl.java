/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory.impl;

import org.springframework.beans.factory.BeanFactory;

import com.elasticpath.tools.sync.beanfactory.SyncBeanFactory;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactoryMutator;

/**
 * The default implementation of {@link SyncBeanFactory}.
 */
public final class SyncBeanFactoryImpl implements SyncBeanFactoryMutator {

	private static SyncBeanFactory syncToolBeanFactory = new SyncBeanFactoryImpl();

	private BeanFactory targetBeanFactory;

	private BeanFactory sourceBeanFactory;

	private SyncBeanFactoryImpl() {
		//singleton
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return bean factory instance
	 */
	public static SyncBeanFactory getInstance() {
		return syncToolBeanFactory;
	}

	/**
	 *
	 * @param <T> the bean class
	 * @param beanName the bean name
	 * @return the bean
	 */
	public <T> T getSourceBean(final String beanName) {
		if (sourceBeanFactory == null) {
			throw new IllegalStateException("A bean was requested from the source system but its context has not been initialized");
		}
		return sourceBeanFactory.getBean(beanName, (Class<T>) null);
	}

	/**
	 *
	 * @param <T> the bean class
	 * @param beanName the bean name
	 * @return the bean
	 */
	public <T> T getTargetBean(final String beanName) {
		if (targetBeanFactory == null) {
			throw new IllegalStateException("A bean was requested from the target system but its context has not been initialized");
		}
		return targetBeanFactory.getBean(beanName, (Class<T>) null);
	}

	/**
	 *
	 * @return the targetBeanFactory
	 */
	public BeanFactory getTargetBeanFactory() {
		return targetBeanFactory;
	}

	/**
	 *
	 * @param targetBeanFactory the targetBeanFactory to set
	 */
	public void setTargetBeanFactory(final BeanFactory targetBeanFactory) {
		this.targetBeanFactory = targetBeanFactory;
	}

	/**
	 *
	 * @return the sourceBeanFactory
	 */
	public BeanFactory getSourceBeanFactory() {
		return sourceBeanFactory;
	}

	/**
	 *
	 * @param sourceBeanFactory the sourceBeanFactory to set
	 */
	public void setSourceBeanFactory(final BeanFactory sourceBeanFactory) {
		this.sourceBeanFactory = sourceBeanFactory;
	}

}
