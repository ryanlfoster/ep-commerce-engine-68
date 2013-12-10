package com.elasticpath.domain.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;

/**
 * The default <code>BeanFactory</code> for ElasticPath which provides a wrapper around a
 * into a Spring <code>ApplicationContext</code> that makes the generics more convenient.
 */
public class ApplicationContextBackedBeanFactoryImpl implements BeanFactory, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	/**
	 * Get the bean with the given name from the Spring
	 * <code>ApplicationContext</code>.
	 *
	 * @param <T> the expected Java type of the bean
	 * @param name the abstract name of the bean to find.
	 * @return instance of the bean with the given id, or null if the bean name
	 *         is not registered with Spring.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(final String name) {
		return (T) this.applicationContext.getBean(name);
	}
	
	/**
	 * Returns the class registered with the specified <code>beanName</code>.
	 *
	 * @param <T> the expected Java type of the bean corresponding to the given name
	 * @param beanName the bean name to get the associated class for.
	 * @return the implementation class registered against the 
	 * <code>beanName</code>. 
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> getBeanImplClass(final String beanName) {
		return (Class<T>) applicationContext.getType(beanName);
	}
	
	
	/**
	 * Set the web application context.
	 * 
	 * @param context the web application context to set.
	 * @throws EpSystemException in case of error
	 */
	public void setApplicationContext(final ApplicationContext context) {
		if (context == null) {
			throw new EpSystemException("ApplicationContext cannot be set to null.");
		}
		this.applicationContext = context;
	}
	
}
