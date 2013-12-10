package com.elasticpath.commons.filter.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * A filter that actively adjusts the caching strategy the browser should use by setting the <b>max-age<b/> attribute of the HTTP
 * <b>Cache-Control</b> header to the age as indicated in the {@code cachingControlEntries}. Note: This filter also forces the <b>Last-Modified</b>
 * time to the current <b>server time</b> and also sets the <b>Expires</b> header to now+max-age.
 */
public final class CachingControlFilter implements Filter {

	private static final int MILLISECONDS_PER_SECOND = 1000;

	private List<CachingControlEntry> cachingControlEntries;

	private static final Logger LOG = Logger.getLogger(CachingControlFilter.class);

	private static final SimpleDateFormat GMT_DATE_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

	static {
		GMT_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Reprents a caching control entry.
	 */
	public static final class CachingControlEntry {
		private String urlPattern;

		private int maxAge;

		/**
		 * Returns the max age a resource should be cached by the browser in seconds.
		 * 
		 * @return the max age in seconds
		 */
		public int getMaxAge() {
			return maxAge;
		}

		/**
		 * Sets the max age in seconds.
		 * 
		 * @param maxAge max age in seconds
		 */
		public void setMaxAge(final int maxAge) {
			this.maxAge = maxAge;
		}

		/**
		 * Returns the url pattern as matched by: <blockquote> {@link java.util.regex.Pattern}.
		 * {@link java.util.regex.Pattern#matches(String,CharSequence) matches}(</tt><i>regex</i><tt>,</tt> <i>str</i><tt>)</tt></blockquote>
		 * 
		 * @return the regular expression pattern
		 */
		public String getUrlPattern() {
			return urlPattern;
		}

		/**
		 * Sets the url pattern as matched by: <blockquote> {@link java.util.regex.Pattern}.
		 * {@link java.util.regex.Pattern#matches(String,CharSequence) matches}(</tt><i>regex</i><tt>,</tt> <i>str</i><tt>)</tt></blockquote>
		 * 
		 * @param urlPattern the regular expression to match on
		 */
		public void setUrlPattern(final String urlPattern) {
			this.urlPattern = urlPattern;
		}
	}

	/**
	 * Called by the web container to indicate to a filter that it is being placed into service. The servlet container calls the init method exactly
	 * once after instantiating the filter. The init method must complete successfully before the filter is asked to do any filtering work.
	 * 
	 * @param filterConfig the filerConfig.
	 */
	public void init(final FilterConfig filterConfig) {
		// donothing
	}

	/**
	 * Called by the web container to indicate to a filter that it is being taken out of service. This method is only called once all threads within
	 * the filter's doFilter method have exited or after a timeout period has passed. After the web container calls this method, it will not call the
	 * doFilter method again on this instance of the filter.
	 */
	public void destroy() { // NOPMD
	}

	/**
	* If this ServletRequest is a HttpServletRequest, the filter will iterate through the control entries it has to find and
	* will add it's caching suggestions to the header.  Note: The <b>first</b> entry found in the list will be used.
	 * 
	 * @param inRequest the request
	 * @param inResponse the response
	 * @param inFilterChain the filter chain
	 * @throws ServletException if something goes wrong
	 * @throws IOException  if something goes wrong
	 */
	public void doFilter(final ServletRequest inRequest, final ServletResponse inResponse, final FilterChain inFilterChain) throws IOException,
			ServletException {
		if (!isHttpServletRequest(inRequest)) {
			inFilterChain.doFilter(inRequest, inResponse);
			return;
		}
		final HttpServletRequest request = (HttpServletRequest) inRequest;
		final HttpServletResponse response = (HttpServletResponse) inResponse;

		for (CachingControlEntry entry : cachingControlEntries) {
			if (request.getRequestURI().matches(entry.getUrlPattern())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("set caching header for " + request.getRequestURI() + ", max-age=" + entry.getMaxAge());
				}

				String cacheControl = getCacheControlValue(entry, response);
				// Set the response content type
				response.setHeader("Cache-Control", cacheControl);
				
				final Date now = new Date();
				response.setHeader("Last-Modified", getDateTimeStrInGMT(now));
				response.setHeader("Expires", getDateTimeStrInGMT(new Date(now.getTime() + entry.getMaxAge() * MILLISECONDS_PER_SECOND)));

				break;
			}
		}

		inFilterChain.doFilter(inRequest, inResponse);
	}

	/**
	 * Gets the cache control value.  If the response contains the "Set-Cookie" header, we should set "Cache-Control: private".
	 * 
	 * @param entry the entry
	 * @param response the response
	 * @return the cache control value
	 */
	String getCacheControlValue(final CachingControlEntry entry, final HttpServletResponse response) {
		String cacheControl = "max-age=" + entry.getMaxAge();
		if (response.containsHeader("Set-Cookie")) {
			cacheControl = "private," + cacheControl;
		}
		return cacheControl;
	}

	/**
	 * Returns the time string in GMT time zone formatted as per RFC 850.
	 * 
	 * @param date a Java date object
	 * @return an RFC 850 formatted time 
	 */
	public String getDateTimeStrInGMT(final Date date) {
		String formattedDate;
		synchronized (GMT_DATE_FORMATTER) {
			formattedDate = GMT_DATE_FORMATTER.format(date);
		}
		return formattedDate;
	}

	private boolean isHttpServletRequest(final ServletRequest inRequest) {
		return (inRequest instanceof HttpServletRequest);
	}

	/**
	 * Returns caching control entries.
	 * 
	 * @return caching control entries
	 */
	public List<CachingControlEntry> getCachingControlEntries() {
		return cachingControlEntries;
	}

	/**
	 * Sets the caching control entries.
	 * 
	 * @param cachingControlEntries the caching control entries to set
	 */
	public void setCachingControlEntries(final List<CachingControlEntry> cachingControlEntries) {
		this.cachingControlEntries = cachingControlEntries;
	}
}
