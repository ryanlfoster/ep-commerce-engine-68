package com.elasticpath.commons.beanframework;

import org.springframework.beans.factory.FactoryBean;

/**
 * An implementation of {@link FactoryBean} that does nothing. This can be useful for overriding beans during testing.
 * 
 * @param <T> the generic type of the object being created
 */
public class NoOpProxyFactoryBean<T> implements FactoryBean<T> {

	private Class<?> serviceInterface;

	@Override
	public T getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Class<?> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(final Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

}