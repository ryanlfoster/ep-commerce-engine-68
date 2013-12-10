/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination.impl;

import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.PaginatorFactory;

/**
 * The default implementation of a {@link PaginatorFactory} that uses Spring configuration
 * to register the paginators in the factory.
 */
public class PaginatorFactoryImpl implements PaginatorFactory {

	private Map<Class< ? >, String> paginators;
	private BeanFactory beanFactory;

	@Override
	public <T> Paginator<T> createPaginator(final Class<T> objectClass, final PaginationConfig paginationConfig) {
		Paginator<T> paginator = findPaginator(objectClass);
		if (paginator == null) {
			return null;
		}
		// initialise the paginator with the provided settings
		paginator.init(paginationConfig);
		
		return paginator;
	}
	

	/**
	 * Finds a paginator.
	 * 
	 * @param <T> the type of the paginator
	 * @param objectClass the object class
	 * @return a paginator instance or null if not found
	 */
	protected <T> Paginator<T> findPaginator(final Class<T> objectClass) {
		String beanName = paginators.get(objectClass);
		if (beanName == null) {
			return null;
		}
		return beanFactory.getBean(beanName);
	}
	
	
	/**
	 *
	 * @param paginators the paginator beans to set mapped to their supported class
	 */
	public void setPaginators(final Map<Class< ? >, String> paginators) {
		this.paginators = paginators;
	}


	/**
	 *
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
