package com.elasticpath.domain.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.elasticpath.base.Initializable;

/**
 * A Spring {@link BeanPostProcessor} that will invoke {@link Initializable#initialize()}.
 */
public class InitializableBeanPostProcessorImpl implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		// Set default values for all persistence objects.
		if (bean instanceof Initializable) {
			((Initializable) bean).initialize();
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}
}
