/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * A bean factory that holds references to already existing objects and do provide
 * them when requested by their bean names.
 * <p>
 * This could be used when objects have to be integrated into a Spring context but cannot
 * be injected using a Spring XML file. When creating the Spring context this bean could
 * be used as a parent of the bean factory being constructed.
 */
public class ProxyBeanFactoryImpl extends AbstractBeanFactory {

	private static final Logger LOG = Logger.getLogger(ProxyBeanFactoryImpl.class);
	
	private final Map<String, Object> beanMap = new HashMap<String, Object>();
	
	/**
	 * 
	 * Checks if a bean could be created by this factory.
	 * 
	 * @param beanName the bean name
	 * @return true if the bean could be created
	 */
	@Override
	protected boolean containsBeanDefinition(final String beanName) {
		return beanMap.containsKey(beanName);
	}

	/**
	 * Retrieves a bean from the bean store of this proxy bean factory.
	 * 
	 * @param beanName the bean name
	 * @param mbd the bean definition
	 * @param args the arguments
	 * @return the bean
	 * @throws BeanCreationException if the bean cannot be created
	 */
	@Override
	protected Object createBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) throws BeanCreationException {
		return beanMap.get(beanName);
	}

	/**
	 * Gets the bean definition of a bean by its {@code beanName}.
	 * 
	 * @param beanName the bean name
	 * @return the bean definition. Never {@code null}
	 * @throws BeansException if the bean cannot be created
	 */
	@Override
	protected BeanDefinition getBeanDefinition(final String beanName) throws BeansException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Requested bean definition for bean: " + beanName);
		}
		Object bean = beanMap.get(beanName);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bean object: " + bean);
		}
		if (bean == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return new RootBeanDefinition(bean.getClass());
	}
	
	/**
	 * Adds a proxy bean to this bean factory store.
	 * 
	 * @param beanName the bean name
	 * @param bean the bean object
	 */
	public void addProxyBean(final String beanName, final Object bean) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Adding proxy bean: %s with beanName: %s", bean, beanName));
		}
		beanMap.put(beanName, bean);
	}

	/**
	 * Find a bean of the given type in the bean store.
	 *
	 * @param <T> the bean type
	 * @param requiredType the required class of the bean
	 * @return a bean that is of the given type
	 * @throws BeansException in case of exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(final Class<T> requiredType) throws BeansException {
		for (Object bean : beanMap.values()) {
			if (bean.getClass().equals(requiredType)) {
				return (T) bean;
			}
		}
		return null;
	}

}
