package com.elasticpath.sfweb.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Implementation of {@link org.springframework.web.servlet.LocaleResolver} that retrieves a {@link Locale} from the current {@link CustomerSession}.
 */
public class CustomerSessionLocaleResolver extends AbstractLocaleResolver {

	private SfRequestHelper requestHelper;

	@Override
	public Locale resolveLocale(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);

		if (customerSession == null) {
			throw new IllegalStateException("Customer Session must exist on the HTTP session");
		}

		final Locale locale = customerSession.getLocale();

		if (locale == null) {
			throw new IllegalStateException("Customer Session must contain a non-null Locale");
		}

		return locale;
	}

	/**
	 * <p>
	 * Unsupported operation.
	 * </p>
	 * <p>
	 * <p>
	 * This implementation expects a {@link com.elasticpath.domain.customer.CustomerSession} instance to exist on the session; this instance is
	 * interrogated for its configured Locale.
	 * </p>
	 * <p>
	 * Consider moving {@link com.elasticpath.sfweb.controller.impl.LocaleControllerImpl LocaleControllerImpl} locale-setting functionality to a new
	 * {@link org.springframework.web.servlet.i18n.LocaleChangeInterceptor LocaleChangeInterceptor} class and then implement this method.
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		throw new UnsupportedOperationException("Locale expected to be set via LocaleController");
	}

	protected SfRequestHelper getRequestHelper() {
		return requestHelper;
	}

	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

}
