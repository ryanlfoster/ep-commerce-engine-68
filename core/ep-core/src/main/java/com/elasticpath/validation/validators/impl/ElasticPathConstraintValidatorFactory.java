package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Rewrite of {@link org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory
 * SpringConstraintValidatorFactory} in order to provide a way to get existing beans instead of instantiating new ones.
 * <p>
 * The default spring implementation always used prototype beans and assumed that beans which required resources had
 * them autowired by spring with no support for xml driven injection. This class serves 2 purposes:
 * <ul>
 * <li>provide support for xml-driven injection</li>
 * <li>allow extension of validators by use of bean overridding</li>
 * </ul>
 * To override a validator bean, override the bean having the ID of the fully qualified class name of the validator.
 * 
 * @see org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory
 */
public class ElasticPathConstraintValidatorFactory implements ConstraintValidatorFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
		if (applicationContext.containsBean(key.getName())) {
			return applicationContext.getAutowireCapableBeanFactory().getBean(key.getName(), key);
		}
		return applicationContext.getAutowireCapableBeanFactory().createBean(key);
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
