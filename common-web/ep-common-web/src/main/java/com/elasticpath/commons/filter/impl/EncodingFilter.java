package com.elasticpath.commons.filter.impl;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.elasticpath.commons.exception.EpLicensingCorruptedException;
import com.elasticpath.commons.exception.EpLicensingExpiredException;
import com.elasticpath.commons.exception.EpLicensingInvalidException;
import com.elasticpath.commons.exception.EpLicensingMissingException;

/**
 * Currently, many browsers do not send character encoding information in the Content-Type header of an HTTP request. If an encoding has not been
 * specified by the client request, the container uses a default encoding to parse request parameters. If the client hasn't set the character
 * encoding and the request parameters are encoded with a different encoding than the default, the parameters will be parsed incorrectly. You can use
 * the method setCharacterEncoding in the ServletRequest interface to set the encoding. Since this method must be called prior to parsing any post
 * data or reading any input from the request, this function is a prime application for filters.
 */
@SuppressWarnings("PMD.PreserveStackTrace")
public final class EncodingFilter implements Filter {
	private String encoding = null;

	private static final Logger LOG = Logger.getLogger(EncodingFilter.class);
	private static final String LICENSE_CORRUPTED_URL = "/licensing-error.ep?errorCode=corrupted";
	private static final String LICENSE_EXPIRED_URL = "/licensing-error.ep?errorCode=expired";
	private static final String LICENSE_INVALID_URL = "/licensing-error.ep?errorCode=invalid";
	private static final String LICENSE_MISSING_URL = "/licensing-error.ep?errorCode=missing";

	/**
	 * Called by the web container to indicate to a filter that it is being placed into service. The servlet container calls the init method exactly
	 * once after instantiating the filter. The init method must complete successfully before the filter is asked to do any filtering work.
	 *
	 * @param filterConfig - the filerConfig.
	 */
	public void init(final FilterConfig filterConfig) {
		this.encoding = filterConfig.getInitParameter("encoding");
	}

	/**
	 * Called by the web container to indicate to a filter that it is being taken out of service. This method is only called once all threads within
	 * the filter's doFilter method have exited or after a timeout period has passed. After the web container calls this method, it will not call the
	 * doFilter method again on this instance of the filter.
	 */
	public void destroy() { // NOPMD
	}

	/**
	 * The doFilter method of the Filter is called by the container each time a request/response pair is passed through the chain due to a client
	 * request for a resource at the end of the chain. The FilterChain passed in to this method allows the Filter to pass on the request and response
	 * to the next entity in the chain. In EncodingFilter, we make sure the characterEncoding set is properly.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @param chain - the filter chain
	 * @throws ServletException - if something goes wrong
	 * @throws EpLicensingInvalidException - if license is invalid
	 * @throws EpLicensingMissingException - if license is missing
	 * @throws IOException - the IOException
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, //NOPMD
			EpLicensingInvalidException, EpLicensingMissingException, IOException {

		String encoding = this.encoding;
		request.setCharacterEncoding(encoding);

		//The catch blocks below catch licensing exceptions, which may or may not be the root cause of
		//a servlet exception, and re-throws new instances of the same exception. This is intended to
		//hide the stack trace beyond this point for security reasons.

		if (isWebSphere(request)) { //the "error-page" doesn't function in WebSphere, so use forword to the license upload page.
			handleWebSphereFilter(request, response, chain);
		} else {
			handleFilter(request, response, chain);
		}
	}

	private boolean isWebSphere(final ServletRequest request) {
		String serverInfo = ((HttpServletRequest) request).getSession().getServletContext().getServerInfo();
		if (serverInfo.indexOf("WebSphere") >=  0) {
			return true;
		}
		return false;
	}

	private void handleWebSphereFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) //NOPMD
	   throws ServletException, EpLicensingInvalidException, EpLicensingMissingException, IOException {

			try {
				chain.doFilter(request, response);
			} catch (EpLicensingCorruptedException lce) {
				request.getRequestDispatcher(LICENSE_CORRUPTED_URL).forward(request, response);
			} catch (EpLicensingExpiredException lee) {
				request.getRequestDispatcher(LICENSE_EXPIRED_URL).forward(request, response);
			} catch (EpLicensingInvalidException lie) {
				request.getRequestDispatcher(LICENSE_INVALID_URL).forward(request, response);
			} catch (EpLicensingMissingException lme) {
				request.getRequestDispatcher(LICENSE_MISSING_URL).forward(request, response);

			} catch (Exception e) {
				if (e instanceof ServletException) { // NOPMD (checking root cause, not exception type)
					final Throwable rootCause = ((ServletException) e).getRootCause();

					if (rootCause instanceof EpLicensingCorruptedException) {
						request.getRequestDispatcher(LICENSE_CORRUPTED_URL).forward(request, response);
						return;
					} else if (rootCause instanceof EpLicensingExpiredException) {
						request.getRequestDispatcher(LICENSE_EXPIRED_URL).forward(request, response);
						return;
					} else if (rootCause instanceof EpLicensingInvalidException) {
						request.getRequestDispatcher(LICENSE_INVALID_URL).forward(request, response);
						return;
					} else if (rootCause instanceof EpLicensingMissingException) {
						request.getRequestDispatcher(LICENSE_MISSING_URL).forward(request, response);
						return;
					} else {
						LOG.fatal("Exception caught in EncodingFilter: ", e); //NOPMD
						throw (ServletException) e;
					}
				}
				LOG.fatal("Exception caught in EncodingFilter: ", e);
				throw new ServletException("Failed in EncodingFilter: ", e);

			}

		}

		private void handleFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) //NOPMD
					throws ServletException, EpLicensingInvalidException, EpLicensingMissingException {
			try {
				chain.doFilter(request, response);
			} catch (EpLicensingCorruptedException lce) {
				throw new EpLicensingCorruptedException("License corrupted");
			} catch (EpLicensingExpiredException lee) {
				throw new EpLicensingExpiredException("License expired");
			} catch (EpLicensingInvalidException lie) {
				throw new EpLicensingInvalidException("Invalid licensing");
			} catch (EpLicensingMissingException lme) {
				throw new EpLicensingMissingException("License missing");
			} catch (Exception e) {
				if (e instanceof ServletException) { // NOPMD (checking root cause, not exception type)
					final Throwable rootCause = ((ServletException) e).getRootCause();

					if (rootCause instanceof EpLicensingCorruptedException) {
						throw new EpLicensingCorruptedException("License corrupted");
					} else if (rootCause instanceof EpLicensingExpiredException) {
						throw new EpLicensingExpiredException("License expired");
					} else if (rootCause instanceof EpLicensingInvalidException) {
						throw new EpLicensingInvalidException("Invalid licensing");
					} else if (rootCause instanceof EpLicensingMissingException) {
						throw new EpLicensingMissingException("License missing");
					} else {
						LOG.fatal("Exception caught in EncodingFilter: ", e);
						throw (ServletException) e;
					}
				}
				LOG.fatal("Exception caught in EncodingFilter: ", e);
				throw new ServletException("Failed in EncodingFilter: ", e);
			}
		}


}
