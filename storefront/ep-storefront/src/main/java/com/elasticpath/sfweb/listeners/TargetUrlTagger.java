/**
 * Copyright (c) 2009. ElasticPath Software Inc. All rights reserved.
 */
package com.elasticpath.sfweb.listeners;


import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Obtains the request's target URL and inserts it into a customer session's
 * {@code TagCloud}.
 */
public class TargetUrlTagger implements NewHttpSessionEventListener {

	private static final String TARGET_URL = "TARGET_URL";
	private static final Logger LOG = Logger.getLogger(TargetUrlTagger.class);
	private static final String QUESTION_MARK = "?";
	
	/**
	 * Obtains the request's target URL and inserts it into a customer session's
	 * {@code TagCloud}.
	 * @param session the {@code CustomerSession} containing the {@code TagCloud} to be modified
	 * @param request the request containing the target URL
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		TagSet tagSet = session.getCustomerTagSet();
		
		StringBuffer requestURL = request.getRequestURL();
		if (request.getQueryString() != null) {
			requestURL.append(QUESTION_MARK);
			requestURL.append(request.getQueryString());
		}
		
		addTargetUrlTag(String.valueOf(requestURL), tagSet);		
	}
	
	/**
	 * Adds a tag to the given tag cloud with the given target (request) URL.
	 * @param targetUrl the URL to add
	 * @param tagCloud the tag cloud to which the tag will be added
	 */
	void addTargetUrlTag(final String targetUrl, final TagSet tagCloud) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Adding target URL tag to tag set: " + targetUrl);
		}
		tagCloud.addTag(TARGET_URL, new Tag(targetUrl));
	}
}
