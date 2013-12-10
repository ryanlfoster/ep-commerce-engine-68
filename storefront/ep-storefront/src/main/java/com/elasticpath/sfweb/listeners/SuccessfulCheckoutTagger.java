package com.elasticpath.sfweb.listeners;


import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.ShoppingCartEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * 
 * Set the FIRST_TIME_BUYER to false in case of successful checkout.
 *
 */
public class SuccessfulCheckoutTagger implements ShoppingCartEventListener {
	
	private static final String FIRST_TIME_BUYER = "FIRST_TIME_BUYER";
	private static final Logger LOG = Logger.getLogger(SuccessfulCheckoutTagger.class);

	@Override
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		TagSet tagSet = session.getCustomerTagSet();
		tagSet.addTag(FIRST_TIME_BUYER, new Tag(false));
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updated customer tag cloud with first time buyer tag: " + tagSet);
		}		
	}

}
