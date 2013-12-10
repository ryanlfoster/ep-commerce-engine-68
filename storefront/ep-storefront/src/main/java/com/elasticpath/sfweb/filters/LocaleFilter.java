package com.elasticpath.sfweb.filters;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.LocaleResolver;

import com.elasticpath.commons.constants.WebConstants;

/**
 * Adds the current {@link Locale} to the request.
 */
public class LocaleFilter implements Filter {

	private LocaleResolver localeResolver;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		// do nothing
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;

			final Locale locale = getLocaleResolver().resolveLocale(httpRequest);

			httpRequest.setAttribute(WebConstants.LOCALE_PARAMETER_NAME, locale);
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// do nothing
	}

	public void setLocaleResolver(final LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	protected LocaleResolver getLocaleResolver() {
		return localeResolver;
	}

}
