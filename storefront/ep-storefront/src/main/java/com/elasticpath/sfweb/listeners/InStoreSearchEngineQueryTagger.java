package com.elasticpath.sfweb.listeners;


import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.BrowsingBehaviorEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * 
 * Applies a internal search terms value to the customer tag set. 
 * INSTORE_SEARCH_TERMS.
 * The value of search terms will be taken from request attributes. 
 * 
 */
public class InStoreSearchEngineQueryTagger implements BrowsingBehaviorEventListener {
	
	private static final String INSTORE_SEARCH_TERMS = "INSTORE_SEARCH_TERMS";
	
	private static final Logger LOG = Logger.getLogger(InStoreSearchEngineQueryTagger.class);
	
	/**
	 * Apply internal search terms tag to the given session.
	 * 
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */	
	@Override
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		final String inStoreSearchTerms = request.getParameterValue(WebConstants.REQUEST_KEYWORDS);
		if (inStoreSearchTerms != null) {
			final TagSet tagSet = session.getCustomerTagSet();
			tagSet.addTag(INSTORE_SEARCH_TERMS, new Tag(inStoreSearchTerms));
			if (LOG.isDebugEnabled()) {
				LOG.debug("Adding instore search terms tag to tag set: " + inStoreSearchTerms);
			}
		}
	}

}
