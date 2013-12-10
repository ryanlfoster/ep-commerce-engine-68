/**
 * 
 */
package com.elasticpath.sfweb.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * <p>Parses the referring URL to evidence of coming from a known search engine. If the referring
 * URL is deemed to be a search engine URL then the search terms will be parsed from the URL
 * and inserted into CustomerSession's tag set.</p>
 * 
 * <p>Supported search engines:
 * <ul><li>GOOGLE</li>
 * <li>YAHOO - does not include search terms in its referral URLs</li>
 * <li>MICROSOFT LIVE</li></ul>
 * </p> 
 */
public class ReferralSearchEngineQueryTagger implements NewHttpSessionEventListener {
	private static final String SEARCH_TERMS = "SEARCH_TERMS";

	private static final Logger LOG = Logger.getLogger(ReferralSearchEngineQueryTagger.class);

	private static final String REFERER = "Referer"; //"Referrer" is misspelled in the HTTP world

	private static final String DELIM = "|";
	
	/**
	 * Investigates the request's headers for evidence of a referral URL containing a supported
	 * search engine and, if one is found, attempts to insert any encoded query terms into the
	 * customer session's {@code TagSet}.
	 * @param session the session containing the tag set
	 * @param request the request containing the referral URL
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		List<String> searchTerms = getSearchTermsFromHeader(request.getHeader(REFERER));
		if (!searchTerms.isEmpty()) {
			addSearchEngineQueryTag(searchTerms, session.getCustomerTagSet());
		}
	}

	/**
	 * Parses the search terms from the given header string.
	 * @param header the header string potentially containing search terms from
	 * a supported search engine.
	 * @return the search terms
	 */
	List<String> getSearchTermsFromHeader(final String header) {
		if (!headerContainsParameters(header)) {
			return Collections.emptyList();
		}
		String parameterString = header.substring(header.indexOf('?') + 1);
		List<String> searchTerms = new ArrayList<String>();
		if (header.contains("google")) {
			searchTerms = getGoogleSearchTerms(parameterString);
		} else if (header.contains("yahoo")) {
			searchTerms = getYahooSearchTerms(parameterString);
		} else if (header.contains("live") || header.contains("msn")) {
			searchTerms = getMsnSearchTerms(parameterString);
		} else {
			LOG.debug("Search engine could not be determined from request.");
		}
		return searchTerms;
	}
	
	/**
	 * Determines whether the given header URL string contains Parameters.
	 * @param header the URL string
	 * @return true if the URL ends in parameters, false if not
	 */
	private boolean headerContainsParameters(final String header) {
		if (StringUtils.isEmpty(header)) {
			LOG.debug("No Referer header found.");
			return false;
		}
		if (header.indexOf('?') == -1) {
			LOG.debug("No parameters in referer URL: " + header);
			return false;
		}
		return true;
	}
	
	/**
	 * Parses GOOGLE search terms from the given request header string.
	 * @param parameterString the string potentially containing GOOGLE search terms
	 * @return a list of search terms
	 */
	private List<String> getGoogleSearchTerms(final String parameterString) {
		return getSearchTermsFromParameterString(parameterString, "q=");
	}

	/**
	 * Gets search terms encoded in a parameter string. Search terms are assumed to be referenced
	 * by the given search term parameter (including the '='), and be separated by a '+'.
	 * @param parameterString the full parameter string
	 * @param searchTermParameter the search term parameter identifier including the '=' (e.g. 'q=')
	 * @return the list of search terms encoded in the string
	 */
	private List<String> getSearchTermsFromParameterString(final String parameterString, final String searchTermParameter) {
		StringTokenizer paramTokenizer = new StringTokenizer(parameterString, "&");
		List<String> searchTerms = new ArrayList<String>();
		while (paramTokenizer.hasMoreTokens()) {
			String param = paramTokenizer.nextToken();
			if (param.startsWith(searchTermParameter)) {
				StringTokenizer queryParamTokenizer = new StringTokenizer(param.substring(2), "+");
				while (queryParamTokenizer.hasMoreTokens()) {
					searchTerms.add(queryParamTokenizer.nextToken());
				}
			}
		}
		return searchTerms;
	}
	
	/**
	 * Parses YAHOO search terms from the given request header string.
	 * @param parameterString the string potentially containing YAHOO search terms
	 * @return a list of search terms
	 */
	private List<String> getYahooSearchTerms(final String parameterString) {
		return getSearchTermsFromParameterString(parameterString, "p=");
	}
	
	/**
	 * Parses MSN/LIVE search terms from the given request header string.
	 * 
	 * @param parameterString the string potentially containing MSN LIVE search terms
	 * @return a list of search terms
	 */
	private List<String> getMsnSearchTerms(final String parameterString) {
		return getSearchTermsFromParameterString(parameterString, "q=");
	}
	
	/**
	 * Adds search terms to the given tag set.
	 * @param searchTerms the terms to add
	 * @param tagSet the tag et
	 */
	private void addSearchEngineQueryTag(final List<String> searchTerms, final TagSet tagCloud) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Adding search engine query tag to tag set: " + searchTerms);
		}
		if (searchTerms.isEmpty()) {
			return;
		}
		StringBuffer termsString = new StringBuffer();
		for (String searchTerm : searchTerms) {
			termsString.append(searchTerm + DELIM);
		}
		tagCloud.addTag(SEARCH_TERMS, new Tag(termsString.toString()));
	}
}
