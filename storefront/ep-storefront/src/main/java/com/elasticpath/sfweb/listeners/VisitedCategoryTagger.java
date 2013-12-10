/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.BrowsingBehaviorEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Applies a CATEGORIES_VISITED and category code value to the customer tag set. The value of visited category will be taken from request attributes.
 */
public class VisitedCategoryTagger implements BrowsingBehaviorEventListener {

	private static final Logger LOG = Logger.getLogger(VisitedCategoryTagger.class);

	private static final String CATEGORIES_VISITED = "CATEGORIES_VISITED";

	private static final String CATEGORIES_VISITED_SEPARATOR = ",";

	/**
	 * Apply visited category tag to the given session. No limitation for how many visited categories will be tracked.
	 * 
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {

		final String categoryCode = getCategoryCodeFromRequest(request);

		if (StringUtils.isEmpty(categoryCode)) {
			return; // nothing to do
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Adding visited category tag to tag set: " + categoryCode);
		}

		final TagSet tagSet = session.getCustomerTagSet();
		final Tag visitedCategoriesTag = tagSet.getTagValue(CATEGORIES_VISITED);

		final Set<String> setOfVisitedCategory = new HashSet<String>();
		if (visitedCategoriesTag != null) {

			String visitedCategories = (String) visitedCategoriesTag.getValue();

			if (visitedCategories.equals(categoryCode) || visitedCategories.startsWith(categoryCode + CATEGORIES_VISITED_SEPARATOR)
					|| visitedCategories.endsWith(CATEGORIES_VISITED_SEPARATOR + categoryCode)
					|| visitedCategories.contains(CATEGORIES_VISITED_SEPARATOR + categoryCode + CATEGORIES_VISITED_SEPARATOR)) {
				return;
			}

			setOfVisitedCategory.addAll(Arrays.asList(visitedCategories.split(CATEGORIES_VISITED_SEPARATOR)));
		}

		setOfVisitedCategory.add(categoryCode);
		String tagValue =  StringUtils.join(setOfVisitedCategory.toArray(), CATEGORIES_VISITED_SEPARATOR);
		tagSet.addTag(CATEGORIES_VISITED, new Tag(tagValue));

	}

	/**
	 * Get the category code from http request.
	 * 
	 * @param request http request
	 * @return category code
	 */
	private String getCategoryCodeFromRequest(final HttpServletRequestFacade request) {
		return request.getParameterOrAttributeValue(WebConstants.REQUEST_CID, null);
	}
}
