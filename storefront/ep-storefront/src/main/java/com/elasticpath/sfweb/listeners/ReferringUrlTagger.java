/**
 * 
 */
package com.elasticpath.sfweb.listeners;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Applies a REFERRING_URL {@link TagDefinitionKey} and value to the customer tag et if http request
 * has a referring url in the header.
 */
public class ReferringUrlTagger implements NewHttpSessionEventListener {
	private static final String REFERRING_URL = "REFERRING_URL";

	private static final Logger LOG = Logger.getLogger(ReferringUrlTagger.class);

	private static final String HTTP_HEADER_REFERER = "referer";

	/**
	 * Apply the start shopping tag to the given session.
	 * 
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		TagSet tagSet = session.getCustomerTagSet();
		addReferringUrlTag(tagSet, request.getHeader(HTTP_HEADER_REFERER));
	}
	

	/**
	 * Adds the Referring URL tag to the customer's tag set
	 * Replace existing value if tag already exists as there should be only one referring URL.
	 * 
	 * @param tagSet the tag set to which the tag should be added
	 * @param url the referring url
	 */
	void addReferringUrlTag(final TagSet tagSet, final String url) {
		if (StringUtils.isEmpty(url)) {
			LOG.trace("Referring URL not in header");
			return;
		}
		tagSet.addTag(REFERRING_URL, new Tag(url));
		LOG.debug("Updated customer tag cloud with referring URL: " + url);
	}

}
