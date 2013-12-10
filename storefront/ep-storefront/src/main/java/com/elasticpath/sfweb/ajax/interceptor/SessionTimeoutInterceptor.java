/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.elasticpath.commons.beanframework.MessageSource;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.SessionExpiredException;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * <code>SessionTimeoutInterceptor</code> checks to see if the customer's session has not timed out and if it has, reloads it from the database.
 */
public class SessionTimeoutInterceptor implements MethodInterceptor {

	private SfRequestHelper requestHelper;
	private MessageSource messageSource;

	/**
	 * Do stuff.
	 *
	 * @param methodInvocation the method invocation to be intercepted and validated
	 * @return the return value of the intercepted method
	 * @throws Throwable on error
	 */
	public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
		final Object[] args = methodInvocation.getArguments();
		for (final Object element : args) {
			// do validation
			if (element instanceof HttpServletRequest) {
				final HttpServletRequest request = (HttpServletRequest) element;
				final CustomerSession customerSession = requestHelper.getCustomerSession(request);
				if (customerSession == null) {
					throw new SessionExpiredException(messageSource.getMessage("onepage.errors.sessionExpired", null,
							"Session has expired.", requestHelper.getStoreConfig().getStore().getDefaultLocale()));
				}
			}
		}
		return methodInvocation.proceed();
	}

	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}