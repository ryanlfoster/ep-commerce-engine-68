package com.elasticpath.sfweb.listeners;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;

/**
 * Adds a tag to the customer session's tag cloud indicating the current Selling Channel.
 */
public class SellingChannelTagger implements NewHttpSessionEventListener, CustomerLoginEventListener {
	private static final String SELLING_CHANNEL = "SELLING_CHANNEL";
	private static final Logger LOG = Logger.getLogger(SellingChannelTagger.class);
	
	/**
	 * Adds a tag to the customer session's tag cloud indicating the current Selling Channel.
	 * @param session the customer session
	 * @param request the request object
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		String storeCode = request.getStoreCode();
		if (StringUtils.isEmpty(storeCode)) {
			LOG.error("Selling Channel Code has not been set on the Customer Session's Customer object - cannot tag selling channel.");
			return;
		}
		session.getCustomerTagSet().addTag(SELLING_CHANNEL, new Tag(storeCode));
	}
}
