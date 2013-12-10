/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory;

import org.springframework.beans.factory.BeanFactory;

/**
 * A mutator interface for setting the source/target bean factories.
 */
public interface SyncBeanFactoryMutator extends SyncBeanFactory {

	/**
	 * Sets the target bean factory.
	 * 
	 * @param targetBeanFactory the targetBeanFactory to set
	 */
	void setTargetBeanFactory(final BeanFactory targetBeanFactory);

	/**
	 * Sets the source bean factory.
	 * 
	 * @param sourceBeanFactory the sourceBeanFactory to set
	 */
	void setSourceBeanFactory(final BeanFactory sourceBeanFactory);

}
