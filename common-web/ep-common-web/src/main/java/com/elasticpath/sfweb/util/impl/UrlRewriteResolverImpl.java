package com.elasticpath.sfweb.util.impl;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.UrlUtility;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Used in urlrewritefilter to map URL information into request attributes that 
 * are utilized in the Storefront's controllers for SEO purposes. 
 * 
 * See the urlrewrite.xml file for configuration information.
 * 
 * This class accepts a single parameter 'fieldSeparator' which changes the 
 * string used to tokenize the filename part of the url.  The parameter should
 * be set with the <init-param/> setting un urlrewrite.xml.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class UrlRewriteResolverImpl {

	private static final Logger LOG = Logger.getLogger(UrlRewriteResolverImpl.class.getName());

	private String fieldSeparator = SeoConstants.DEFAULT_SEPARATOR_BETWEEN_TOKENS;
	
	/**
	 * Validator pattern for the category page number parameter.
	 */
	private static final Pattern PAGE_NUMBER_VALIDATOR = Pattern.compile("[0-9]{1,8}");
	
	private final SfRequestHelper requestHelper;
	private final CustomerSessionService customerSessionService;
	private final UrlUtility urlUtility;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public UrlRewriteResolverImpl() {
		ElasticPath elasticPath = ElasticPathImpl.getInstance();
		this.requestHelper = elasticPath.getBean("requestHelper");
		this.urlUtility = elasticPath.getBean("urlUtility");
		customerSessionService = elasticPath.getBean(ContextIdNames.CUSTOMER_SESSION_SERVICE);
	}

	
	/**
	 * Constructor with params.
	 * 
	 * @param requestHelper - request helper
	 * @param urlUtility an UrlUtility class instance
	 * @param customerSessionService {@link CustomerSessionService} to use
	 */
	public UrlRewriteResolverImpl(final SfRequestHelper requestHelper, final UrlUtility urlUtility,
			final CustomerSessionService customerSessionService) {
		this.requestHelper = requestHelper;
		this.urlUtility = urlUtility;
		this.customerSessionService = customerSessionService;
	}


	/**
	 * Converts filter information from the URL (the request's filename) into 
	 * a request attributes for use in the Storefront's controllers.
	 *
	 * @param aRequest the request
	 * @param aResponse not used
	 */
	public void resolve(final HttpServletRequest aRequest, final HttpServletResponse aResponse) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Received request :" + aRequest.getRequestURI());
		}
		
		// Sets the SEO URL for future comparison. This is required because
		// the request URI gets changed when the request is forwarded and we need
		// to save the original request URI for it to be compared at a later stage.
		aRequest.setAttribute(WebConstants.SEO_URL_STRING, urlUtility.decodeUrl2Text(aRequest.getRequestURI()));
		
		// The information we want is in the request's filename.
		final String pageStr = getUriFilename(aRequest.getRequestURI());
		final String[] tokens = pageStr.split(fieldSeparator);
		
		// Process the information into request attributes
		try {
			setupRequestAttributes(tokens, aRequest, aResponse);
		} catch (IOException exc) {
			LOG.warn("Exception occurred while parsing the SEO URL", exc);
		}
		
		LOG.debug("Urlrewrite --- before infomation call");
		processPossibleLocaleChange(aRequest, aResponse);
	}

	/**
	 * Called during creation of this instance so we can process any 
	 * <code>init-param</code>s defined in the urlrwrite.xml file.
	 * 
	 * @param config the servlet config to get init-params from.
	 */
	public void init(final ServletConfig config) {
		// \\Q and \\E quote the string so that it is not interpreted as a 
		// regular expression when used in String.split later.
		fieldSeparator = "\\Q" + getFieldSeparator(config) + "\\E";
	}
	
	/**
	 * Checks the servlet parameters for 'fieldSeparator' and uses that
	 * value if found, otherwise it returns SeoConstants.SEPARTOR_BETWEEN_TOKENS.
	 * If the parameter is null or empty string the default value will be used.
	 * 
	 * @param aRequest the request to read the parameter from.
	 * @return the string to use to tokenize the url's filename with.
	 */
	private String getFieldSeparator(final ServletConfig config) {
		String paramFieldSeparator = config.getInitParameter("fieldSeparator");
		if (paramFieldSeparator != null && !"".equals(paramFieldSeparator)) {
			return paramFieldSeparator;
		}
		return SeoConstants.DEFAULT_SEPARATOR_BETWEEN_TOKENS;
	}
	
	/** 
	 * Perform a check to see if the language should be changed.
	 * 
	 * @param aRequest - request
	 * @param aResponse - response
	 */
	protected void processPossibleLocaleChange(final HttpServletRequest aRequest, final HttpServletResponse aResponse) {
		String localeFromRequest = (String) aRequest.getAttribute(WebConstants.URL_REQUEST_LOCALE); 
		if (localeFromRequest != null && localeFromRequest.length() > 0) {
		    CustomerSession customerSession = requestHelper.getCustomerSession(aRequest);
			Locale newLocale = getLocaleByLanguageStr(localeFromRequest);
			if (!newLocale.equals(customerSession.getLocale())) {
				customerSession.setLocale(newLocale);
				customerSessionService.update(customerSession);
				aRequest.setAttribute(WebConstants.LOCALE_PARAMETER_NAME, newLocale);
			}
		}
	}
	
	/**
	 * Using a language String finds the afferent locale.
	 * @param localeString
	 * @return
	 */
	private Locale getLocaleByLanguageStr(final String localeString) {
		Store store = requestHelper.getStoreConfig().getStore();
		for (Locale locale : store.getSupportedLocales()) {
			if (locale.toString().equals(localeString)) {
				return locale;
			}
		}
		return store.getDefaultLocale();
	}

	
	/**
	 * Parse the specified filter tokens and setup various request attributes from the
	 * parsed values.
	 * 
	 * Attributes that may be created (depending on the tokens passed in):
	 * <ul>
	 * 	<li><code>WebConstants.REQUEST_CID</code>
	 *  <li><code>WebConstants.REQUEST_PID</code>
	 *  <li><code>WebConstants.REQUEST_PAGE_NUM</code>
	 *  <li><code>WebConstants.REQUEST_FILTERS</code>
	 * </ul>
	 * 
	 * @param tokens the tokens to parse.
	 * @param aRequest the request to set attributes on.
	 */
	private void setupRequestAttributes(final String[] tokens,
			final HttpServletRequest aRequest, final HttpServletResponse response) throws IOException {
		
		final StringBuffer sbfFilters = new StringBuffer();
		for (int i = 0; i < tokens.length; i++) {
			final String decodedToken = decodeString(tokens[i]);
			if (decodedToken.startsWith(SeoConstants.CATEGORY_PREFIX)) {
				final String value = getValue(decodedToken, SeoConstants.CATEGORY_PREFIX);
				aRequest.setAttribute(WebConstants.REQUEST_CID, value);
				sbfFilters.append(decodedToken).append(' ');
				
			} else if (decodedToken.startsWith(SeoConstants.PRODUCT_PREFIX)) {
				final String value = getValue(decodedToken, SeoConstants.PRODUCT_PREFIX);
				aRequest.setAttribute(WebConstants.REQUEST_PID, value);
				// Do not append a filter element for products because it doesn't 
				// make sense to filter on a specific product.
				
			} else if (decodedToken.startsWith(SeoConstants.PRICE_FILTER_PREFIX)) {
				sbfFilters.append(decodedToken).append(' ');
				
			} else if (decodedToken.startsWith(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX)) {
				sbfFilters.append(decodedToken).append(' ');
				
			} else if (decodedToken.startsWith(SeoConstants.ATTRIBUTE_FILTER_PREFIX)) {
				sbfFilters.append(decodedToken).append(' ');
				
			} else if (decodedToken.startsWith(SeoConstants.BRAND_FILTER_PREFIX)) {
				final String value = getValue(decodedToken, SeoConstants.BRAND_FILTER_PREFIX);
				final String brandFilterId = getBrandFilterId(value);
				sbfFilters.append(brandFilterId).append(' ');
				
			} else if (decodedToken.startsWith(SeoConstants.PAGE_NUMBER_PREFIX)) {
				final String value = getValue(decodedToken, SeoConstants.PAGE_NUMBER_PREFIX);
				Matcher matcher = PAGE_NUMBER_VALIDATOR.matcher(value);
				if (!matcher.matches()) {
					// invalid page number, send "page not found" to prevent 
					// the system responding with a page to malicious urls.
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				aRequest.setAttribute(WebConstants.REQUEST_PAGE_NUM, value);
				// Do not append a filter element for page numbers because it doesn't
				// make sense to filter by a specific page.
			} else if (decodedToken.equals(SeoConstants.SITEMAP_PREFIX)) {
				// Do nothing, this block explicitly allows sitemap urls through
				continue;
			} else {
				// invalid request parameter, send "page not found" to prevent 
				// the system responding with a page to malicious urls.
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}

		if (sbfFilters.length() > 0) {
			aRequest.setAttribute(WebConstants.REQUEST_FILTERS, sbfFilters.substring(0, sbfFilters.length() - 1));
		}
	}


	/**
	 * Decodes a token into the original string set by the SeoUrlBuilder.
	 */
	private String decodeString(final String token) {
		
		return this.urlUtility.decodeUrl2Text(token);
	}


	/**
	 * Extract the filename part of a URI (everything after the last '/' and 
	 * before the query string in a URL) not including the 
	 * <code>SeoConstants.SUFFIX</code> extension.
	 * 
	 * @param uri the URI to extract the filename from.
	 * @return the filename part of the URI.
	 */
	private String getUriFilename(final String uri) {
		int endPos = uri.indexOf(SeoConstants.SUFFIX);
		if (endPos < 0) {
			endPos = uri.length();
		}

		return uri.substring(uri.lastIndexOf('/') + 1, endPos);
	}

	private String getBrandFilterId(final String value) {
		return SeoConstants.BRAND_FILTER_PREFIX + value;
	}

	private String getValue(final String token, final String prefix) {
		final int startPos = prefix.length();
		int endPos = token.indexOf(SeoConstants.SUFFIX);
		if (endPos < 0) {
			endPos = token.length();
		}

		return token.substring(startPos, endPos);
	}
	
}
