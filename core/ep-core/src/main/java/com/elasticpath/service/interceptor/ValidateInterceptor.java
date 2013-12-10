/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.interceptor;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springmodules.validation.commons.DefaultBeanValidator;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.base.exception.EpServiceException;

/**
 * <code>ValidateInterceptor</code> intercepts service calls and validate the domain objects passed in.
 */
public class ValidateInterceptor implements MethodInterceptor {
	private static final Logger LOG = Logger.getLogger(ValidateInterceptor.class);

	private DefaultBeanValidator defaultBeanValidator;

	/**
	 * Validate the arguments to service calls if it is a domain object.
	 *
	 * @param methodInvocation the method invocation to be intercepted and validated
	 * @return the return value of the intercepted method
	 * @throws Throwable on error
	 */
	public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
		Object[] args = methodInvocation.getArguments();
		for (int i = 0; i < args.length; i++) {
			// do validation
			if (args[i] instanceof EpDomain) {
				EpDomain validatable = (EpDomain) args[i];
				if (LOG.isDebugEnabled()) {
					LOG.debug("bindAndValidate calling for: " + validatable.toString());
				}
				this.validate(validatable);
			}
		}
		return methodInvocation.proceed();
	}

	/**
	 * Set the validator.
	 * @param defaultBeanValidator the validator
	 */
	public void setDefaultBeanValidator(final DefaultBeanValidator defaultBeanValidator) {
		this.defaultBeanValidator = defaultBeanValidator;
	}


	/**
	 * Validate the domain object.
	 *
	 * @param epDomain the domain object to be validated
	 * @throws EpDomainException if there is a problem
	 * @throws IllegalArgumentException thrown if the validator doesn't support the specified class
	 */
	public void validate(final EpDomain epDomain) throws EpDomainException {
		Class<?> clazz = epDomain.getClass();
		if (this.defaultBeanValidator == null) {
			throw new EpDomainException("Cannot validate as Validator not wired against: " + this);
		}
		if (this.defaultBeanValidator.supports(clazz)) {
			Errors errors = new BindException(epDomain, clazz.getName());
			this.defaultBeanValidator.validate(epDomain, errors);
			if (errors.getErrorCount() > 0) {
				List<ObjectError> errList = errors.getAllErrors();
				int errListSize = errList.size();
				StringBuffer errStrBuf = new StringBuffer("The following field(s) is(are) invalid: ");
				for (int i = 0; i < errListSize; i++) {
					FieldError fieldError = (FieldError) errList.get(i);
					errStrBuf.append(fieldError.getField());
					if (i == errListSize - 1) {
						errStrBuf.append('.');
					} else {
						errStrBuf.append(',');
					}
				}
				throw new EpServiceException(errStrBuf.toString(), (BindException) errors);
			}
		}
//		else {
//			throw new IllegalArgumentException("Validator " + this.defaultBeanValidator.getClass().getName() + " does not support this class: "
//					+ clazz.getName());
//		}
	}
}
